import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {LoggingService} from "../../base_classes/logging_and_notifications/logging.service";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {Location} from "@angular/common";
import {switchMap} from "rxjs/operators";
import {Driver} from "../model/driver";
import {DriverService} from "../services/driver.service";

@Component({
  selector: 'app-driver-detail',
  templateUrl: './driver-detail.component.html',
  styleUrls: ['./driver-detail.component.scss']
})
export class DriverDetailComponent implements OnInit {

  private editDriverUrl: string = 'http://localhost:8080/api/driver/edit';
  public editDriverFailedMessage = null;
  public editDriverForm: FormGroup;
  public driver: Driver;

  constructor(private router: Router,
              private location: Location,
              private httpClient: HttpClient,
              private loggingService: LoggingService,
              private notificationService: NotificationService,
              private route: ActivatedRoute,
              private driverService: DriverService
  ) {
  }

  ngOnInit(): void {
    this.editDriverForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.email]),
      car: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      phone: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.pattern("\\+?[0-9]{3,}")]),
      firstName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(256)])});

    this.route.params
      .pipe(switchMap((params: Params) => this.driverService.getDriver(+params['id'])))
      .subscribe(driver => this.driver = driver);
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.editDriverForm.controls[controlName].hasError(errorName);
  };

  public onCancel = () => {
    this.location.back();
  };

  public doEditDriver = (editDriverFormValue) => {
    if (this.editDriverForm.valid) {
      this.editDriver(
        editDriverFormValue.email,
        editDriverFormValue.car,
        editDriverFormValue.phone,
        editDriverFormValue.firstName,
        editDriverFormValue.lastName
      );
    }
  };

  private editDriver(email: string, car: string, phone: string, firstName: string,
                    lastName: string) {
    this.httpClient.post<Driver>(
      this.editDriverUrl + "/" + this.driver.id,
      {
        email: email,
        car: car,
        phone: phone,
        firstName: firstName,
        lastName: lastName
      })
      .subscribe(
        result => {
          this.router.navigateByUrl("/drivers");
        }
      );
  }

}
