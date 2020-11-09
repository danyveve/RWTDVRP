import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './base_components/home/home.component';
import {LoginComponent} from './base_components/login/login.component';
import {NotFoundComponent} from './base_components/not-found/not-found.component';
import {UnauthorizedComponent} from './base_components/unauthorized/unauthorized.component';
import {ForbiddenComponent} from './base_components/forbidden/forbidden.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {HttpErrorInterceptor} from "./base_classes/error_handlers/http-error.interceptor";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {GlobalErrorHandler} from "./base_classes/error_handlers/global.error.handler";
import {rollbarFactory, RollbarService} from "./base_classes/logging_and_notifications/rollbar";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FlexLayoutModule} from "@angular/flex-layout";
import {RegisterComponent} from './base_components/register/register.component';
import {MatSelectModule} from "@angular/material/select";
import {HttpExtraHeadersInterceptor} from "./base_classes/error_handlers/http-extra-headers.interceptor";
import {VrpListComponent} from './vrp_module/vrp-list/vrp-list.component';
import {VrpAddComponent} from './vrp_module/vrp-add/vrp-add.component';
import {NavbarComponent} from './base_components/navbar/navbar.component';
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {AgmCoreModule} from '@agm/core';
import {GmapAddComponent} from './vrp_module/gmap-add/gmap-add.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {VrpDetailsComponent} from './vrp_module/vrp-details/vrp-details.component';
import {MatMenuModule} from "@angular/material/menu";
import {EditProfileComponent} from './base_components/edit-profile/edit-profile.component';
import {MatRadioModule} from "@angular/material/radio";
import {DriverListComponent} from './driver_module/driver-list/driver-list.component';
import {DriverDetailComponent} from './driver_module/driver-detail/driver-detail.component';
import {DriverAddComponent} from './driver_module/driver-add/driver-add.component';
import {ShowIfLoggedInDirective} from "./base_classes/session_management/logged-directive";
import {ShowIfAdminDirective} from "./base_classes/session_management/is-admin-directive";
import {LoggedInGuard} from "./base_classes/session_management/logged-in-guard";
import {AdminGuard} from "./base_classes/session_management/admin-guard";


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    NotFoundComponent,
    UnauthorizedComponent,
    ForbiddenComponent,
    RegisterComponent,
    VrpListComponent,
    VrpAddComponent,
    NavbarComponent,
    GmapAddComponent,
    VrpDetailsComponent,
    EditProfileComponent,
    DriverListComponent,
    DriverDetailComponent,
    DriverAddComponent,
    ShowIfLoggedInDirective,
    ShowIfAdminDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    FlexLayoutModule,
    MatSelectModule,
    MatIconModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatRadioModule,
    AgmCoreModule.forRoot({
      apiKey: '<your_key>',
      libraries: ['places']
    }),
  ],
  providers: [
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    },
    {
      provide: RollbarService,
      useFactory: rollbarFactory
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpExtraHeadersInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    LoggedInGuard, AdminGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
