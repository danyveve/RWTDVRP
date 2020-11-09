import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {LoggingService} from "../../base_classes/logging_and_notifications/logging.service";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {MustMatch} from "../../utils/form.utils";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  private registerUrl: string = 'http://localhost:8080/api/user/register';
  public registerFailedMessage = null;
  public registerForm: FormGroup;

  constructor(private router: Router,
              private location: Location,
              private httpClient: HttpClient,
              private loggingService: LoggingService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.registerForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.email]),
      username: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      password: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      passwordConfirmation: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      phone: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.pattern("\\+?[0-9]{3,}")]),
      firstName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      // company: new FormControl('', [Validators.required])
    }, {
      validators: [MustMatch("password", "passwordConfirmation")]
    });
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.registerForm.controls[controlName].hasError(errorName);
  };

  public onCancel = () => {
    this.location.back();
  };

  public doRegister = (registerFormValue) => {
    if (this.registerForm.valid) {
      this.register(
        registerFormValue.email,
        registerFormValue.username,
        registerFormValue.password,
        registerFormValue.passwordConfirmation,
        registerFormValue.phone,
        registerFormValue.firstName,
        registerFormValue.lastName
      );
    }
  };

  private register(email: string, username: string, password: string,
                   passwordConfirmation: string, phone: string, firstName: string,
                   lastName: string) {
    this.httpClient.post(
      this.registerUrl,
      {
        email: email,
        username: username,
        password: password,
        passwordConfirmation: passwordConfirmation,
        phone: phone,
        firstName: firstName,
        lastName: lastName
      })
      .subscribe(
        result => {
          this.router.navigateByUrl("/login");
        },
        (error: HttpErrorResponse) => {
          if (error.status === 0) {
            this.registerFailedMessage = "Something went wrong. Maybe the server is down?";
          }
          throw error;
        }
      );
  }
}
