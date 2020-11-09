import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {LoggingService} from "../../base_classes/logging_and_notifications/logging.service";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {Location} from '@angular/common';
import {MustMatch} from "../../utils/form.utils";
import {User} from "../../user_module/model/user";
import {UserService} from "../../user_module/service/user.service";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {
  private editProfileUrl: string = 'http://localhost:8080/api/user/edit/profile';
  public editProfileFailedMessage = null;
  public editProfileForm: FormGroup;
  public currentUser: User;

  constructor(private router: Router,
              private location: Location,
              private httpClient: HttpClient,
              private loggingService: LoggingService,
              private notificationService: NotificationService,
              private userService: UserService) {
  }

  ngOnInit(): void {
    this.editProfileForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.email]),
      password: new FormControl('', [Validators.maxLength(256)]),
      passwordConfirmation: new FormControl('', [Validators.maxLength(256)]),
      phone: new FormControl('', [Validators.required, Validators.maxLength(256), Validators.pattern("\\+?[0-9]{3,}")]),
      firstName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      lastName: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      // company: new FormControl('', [Validators.required])
    }, {
      validators: [MustMatch("password", "passwordConfirmation")]
    });

    this.getCurrentUser();
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.editProfileForm.controls[controlName].hasError(errorName);
  };

  public onCancel = () => {
    this.location.back();
  };

  public doEditProfile = (editProfileFormValue) => {
    if (this.editProfileForm.valid) {
      this.editProfile(
        editProfileFormValue.email,
        editProfileFormValue.password,
        editProfileFormValue.passwordConfirmation,
        editProfileFormValue.phone,
        editProfileFormValue.firstName,
        editProfileFormValue.lastName
      );
    }
  };

  private editProfile(email: string, password: string,
                   passwordConfirmation: string, phone: string, firstName: string,
                   lastName: string) {
    this.httpClient.post(
      this.editProfileUrl,
      {
        email: email,
        password: password,
        passwordConfirmation: passwordConfirmation,
        phone: phone,
        firstName: firstName,
        lastName: lastName
      })
      .subscribe(
        result => {
          this.router.navigateByUrl("/home");
        }
      );
  }

  private getCurrentUser() {
    this.userService.getCurrentUser()
      .subscribe(user => this.currentUser = user);
  }
}
