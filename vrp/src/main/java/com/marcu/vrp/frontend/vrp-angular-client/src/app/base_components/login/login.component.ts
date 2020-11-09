import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Location} from '@angular/common';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {LoggingService} from "../../base_classes/logging_and_notifications/logging.service";
import {NotificationService} from "../../base_classes/logging_and_notifications/notification.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../user_module/service/user.service";
import {SessionState, SessionStore} from "../../base_classes/session_management/session-store";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  private loginUrl: string = 'http://localhost:8080/login';
  public loginFailedMessage = null;
  public loginForm: FormGroup;

  constructor(private router: Router,
              private location: Location,
              private httpClient: HttpClient,
              private loggingService: LoggingService,
              private notificationService: NotificationService,
              private userService: UserService,
              private authStore: SessionStore) {
  }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required, Validators.maxLength(256)]),
      password: new FormControl('', [Validators.required, Validators.maxLength(256)])
    });
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.loginForm.controls[controlName].hasError(errorName);
  };

  public onCancel = () => {
    this.location.back();
  };

  public doLogin = (loginFormValue) => {
    if (this.loginForm.valid) {
      this.login(loginFormValue.username, loginFormValue.password);
    }
  };

  private login(username: string, password: string) {
    this.httpClient.post(this.loginUrl, {username: username, password: password})
      .subscribe(
        result => {
          this.userService.getCurrentUser()
            .subscribe((result) => {
              this.authStore.login({id: result.id, role: result.role.name, username: result.username} as SessionState);
              this.router.navigateByUrl("/home");
            });
        },
        (error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.loginFailedMessage = "Incorrect username or password!";
          } else {
            this.loginFailedMessage = "Something went wrong. Maybe the server is down?";
            throw error;
          }
        }
      );
  }
}
