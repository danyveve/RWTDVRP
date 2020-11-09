import {DriverService} from "../../driver_module/services/driver.service";
import {Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {MapsAPILoader, MouseEvent as AGMMouseEvent} from "@agm/core";
import {Driver} from "../../driver_module/model/driver";
import {GeographicPoint} from "../model/geographic-point";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {NotificationType} from "../../base_classes/logging_and_notifications/notification-type";
import {VrpService} from "../services/vrp.service";
import {StartVrpRequest} from "../model/start-vrp-request";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {error} from "selenium-webdriver";

declare const google: any;

@Component({
  selector: 'app-gmap-add',
  templateUrl: './gmap-add.component.html',
  styleUrls: ['./gmap-add.component.scss']
})
export class GmapAddComponent implements OnInit {
  latitude: number;
  longitude: number;
  zoom: number;
  address: string;
  private geoCoder;
  drivers: Array<Driver> = [];
  selectedDrivers: Array<Driver> = [];
  depot: GeographicPoint = null;
  deliveryPoints: Array<GeographicPoint> = [];
  preferredTime: string;
  startClicked: boolean = false;

  @ViewChild('search')
  public searchElementRef: ElementRef;

  constructor(
    private mapsAPILoader: MapsAPILoader,
    private ngZone: NgZone,
    private driverService: DriverService,
    private notificationService: NotificationService,
    private vrpService: VrpService,
    private router: Router,
    private location: Location
  ) {
  }

  ngOnInit(): void {
    GmapAddComponent.styleMaps();
    this.loadPlacesAutocomplete();
    this.getDrivers();
  }

  // Get Current Location Coordinates
  private setCurrentLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 8;
        this.getAddress(this.latitude, this.longitude);
      });
    }
  }

  markerDragEnd($event: AGMMouseEvent) {
    this.latitude = $event.coords.lat;
    this.longitude = $event.coords.lng;
    this.getAddress(this.latitude, this.longitude);
  }

  getAddress(latitude, longitude) {
    this.geoCoder.geocode({ 'location': { lat: latitude, lng: longitude } }, (results, status) => {
      if (status === 'OK') {
        if (results[0]) {
          this.zoom = 12;
          this.address = results[0].formatted_address;
        } else {
          window.alert('No results found');
        }
      } else {
        window.alert('Geocoder failed due to: ' + status);
      }

    });
  }

  private static styleMaps() {
    let mapsContainer = (<HTMLElement>document.getElementsByClassName("agm-map-container-inner").item(0));
    mapsContainer.style.borderRadius = "25px";
    mapsContainer.style.border = "2px solid #424242";

    (<HTMLInputElement> document.getElementById("preferredTimePick")).min = new Date().toISOString().slice(0,-8);
  }

  private loadPlacesAutocomplete() {
    //load Places Autocomplete
    this.mapsAPILoader.load().then(() => {
      this.setCurrentLocation();
      this.geoCoder = new google.maps.Geocoder;

      let autocomplete = new google.maps.places.Autocomplete(this.searchElementRef.nativeElement);
      autocomplete.addListener("place_changed", () => {
        this.ngZone.run(() => {
          //get the place result
          let place: google.maps.places.PlaceResult = autocomplete.getPlace();

          //verify result
          if (place.geometry === undefined || place.geometry === null) {
            return;
          }

          //set latitude, longitude and zoom
          this.latitude = place.geometry.location.lat();
          this.longitude = place.geometry.location.lng();
          this.getAddress(this.latitude, this.longitude);
          this.zoom = 12;
        });
      });
    });
  }

  private getDrivers() {
    this.driverService.getDrivers()
      .subscribe(
        drivers => {
          this.drivers = drivers;
        }
      )
  }

  markAsDepot() {
    for (let i = 0; i < this.deliveryPoints.length; i++) {
      let current = this.deliveryPoints[i];
      if (this.address === current.address && this.latitude == current.latitude && this.longitude == current.longitude){
        this.notificationService.addMessage("This point is already added as a Delivery Point!", NotificationType.ERROR);
        return;
      }
    }
    this.depot = {latitude: this.latitude, longitude: this.longitude, address: this.address} as GeographicPoint;
  }

  addDeliveryPoint() {
    let toBeAdded = {latitude: this.latitude, longitude: this.longitude, address: this.address} as GeographicPoint;
    if (this.depot && toBeAdded.latitude === this.depot.latitude && toBeAdded.longitude === this.depot.longitude && toBeAdded.address === this.depot.address) {
      this.notificationService.addMessage("This point is already marked as depot!", NotificationType.ERROR);
      return;
    }
    for (let i = 0; i < this.deliveryPoints.length; i++) {
      let current = this.deliveryPoints[i];
      if (toBeAdded.address === current.address && toBeAdded.latitude == current.latitude && toBeAdded.longitude == current.longitude){
        this.notificationService.addMessage("This delivery point is already added!", NotificationType.ERROR);
        return;
      }
    }
    this.deliveryPoints.push(toBeAdded);
  }

  deleteDepot() {
    this.depot = null;
    this.notificationService.addMessage("Depot removed!", NotificationType.SUCCESS);
  }

  deleteDeliveryPoint(deliveryPoint: GeographicPoint) {
    this.deliveryPoints = this.deliveryPoints.filter(dp => dp !== deliveryPoint);
    this.notificationService.addMessage("Delivery Point removed!", NotificationType.SUCCESS);
  }

  driverChange(checked: boolean, driver: Driver) {
    if (checked) {
      this.selectedDrivers.push(driver);
    } else {
      this.selectedDrivers = this.selectedDrivers.filter(d => d !== driver);
    }
  }

  startSolvingVrp() {
    if (!this.depot ) {
      throw Error("You must provide a depot!");
    }

    if (!this.preferredTime ) {
      throw Error("You must provide a preferred departure time!");
    }
    if (new Date(this.preferredTime) < new Date()) {
      throw Error("You must provide a future departure time (at least one minute apart from current time)!");
    }

    if (this.selectedDrivers.length === 0 ) {
      throw Error("You must provide at least one driver!");
    }

    if (this.deliveryPoints.length === 0 ) {
      throw Error("You must provide at least one delivery point!");
    }

    if (this.selectedDrivers.length > this.deliveryPoints.length) {
      throw Error("The number of drivers must be smaller or equal ( <= ) than the number of delivery points!");
    }

    this.startClicked = true;
    this.preferredTime = new Date(this.preferredTime).toISOString();
    this.vrpService.startVrp({drivers: this.selectedDrivers, depot: this.depot, deliveryPoints: this.deliveryPoints, preferredDepartureTime: this.preferredTime} as StartVrpRequest)
      .subscribe(
        id => this.router.navigateByUrl("/vrp/detail/" + id.toString()),
        error => {
          this.startClicked = false;
          throw error;
        }
      );
  }

  cancelVrpStart() {
    this.location.back();
  }
}
