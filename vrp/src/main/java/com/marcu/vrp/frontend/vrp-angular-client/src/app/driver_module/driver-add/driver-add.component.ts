import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {LoggingService} from "../../base_classes/logging_and_notifications/logging.service";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {Location} from "@angular/common";
import {Driver} from "../model/driver";

@Component({
  selector: 'app-driver-add',
  templateUrl: './driver-add.component.html',
  styleUrls: ['./driver-add.component.scss']
})
export class DriverAddComponent implements OnInit {
  private addDriverUrl: string = 'http://localhost:8080/api/driver/add';
  public addDriverFailedMessage = null;
  public addDriverForm: FormGroup;

  constructor(private router: Router,
              private location: Location,
              private httpClient: HttpClient,
              private loggingService: LoggingService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.addDriverForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.email]),
      car: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      phone: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.pattern("\\+?[0-9]{3,}")]),
      firstName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(256)])});
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.addDriverForm.controls[controlName].hasError(errorName);
  };

  public onCancel = () => {
    this.location.back();
  };

  public doAddDriver = (addDriverFormValue) => {
    if (this.addDriverForm.valid) {
      this.addDriver(
        addDriverFormValue.email,
        addDriverFormValue.car,
        addDriverFormValue.phone,
        addDriverFormValue.firstName,
        addDriverFormValue.lastName
      );
    }
  };

  private addDriver(email: string, car: string, phone: string, firstName: string,
                   lastName: string) {
    this.httpClient.post<Driver>(
      this.addDriverUrl,
      {
        email: email,
        car: car,
        phone: phone,
        firstName: firstName,
        lastName: lastName
      })
      .subscribe(
        result => {
          this.router.navigateByUrl("/driver/detail/" + result.id);
        }
      );
  }

}
