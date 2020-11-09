import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from "./base_components/home/home.component";
import {LoginComponent} from "./base_components/login/login.component";
import {NotFoundComponent} from "./base_components/not-found/not-found.component";
import {UnauthorizedComponent} from "./base_components/unauthorized/unauthorized.component";
import {ForbiddenComponent} from "./base_components/forbidden/forbidden.component";
import {RegisterComponent} from "./base_components/register/register.component";
import {VrpListComponent} from "./vrp_module/vrp-list/vrp-list.component";
import {VrpAddComponent} from "./vrp_module/vrp-add/vrp-add.component";
import {VrpDetailsComponent} from "./vrp_module/vrp-details/vrp-details.component";
import {EditProfileComponent} from "./base_components/edit-profile/edit-profile.component";
import {DriverListComponent} from "./driver_module/driver-list/driver-list.component";
import {DriverDetailComponent} from "./driver_module/driver-detail/driver-detail.component";
import {DriverAddComponent} from "./driver_module/driver-add/driver-add.component";
import {LoggedInGuard} from "./base_classes/session_management/logged-in-guard";
import {AdminGuard} from "./base_classes/session_management/admin-guard";


const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'edit-profile', component: EditProfileComponent, canActivate: [LoggedInGuard]},
  {path: 'drivers', component: DriverListComponent, canActivate: [AdminGuard]},
  {path: 'driver/add', component: DriverAddComponent, canActivate: [AdminGuard]},
  {path: 'driver/detail/:id', component: DriverDetailComponent, canActivate: [AdminGuard]},
  {path: 'unauthorized', component: UnauthorizedComponent},
  {path: 'forbidden', component: ForbiddenComponent},
  {path: 'vrp', component: VrpListComponent, canActivate: [LoggedInGuard]},
  {path: 'vrp/add', component: VrpAddComponent, canActivate: [LoggedInGuard]},
  {path: 'vrp/detail/:id', component: VrpDetailsComponent, canActivate: [LoggedInGuard]},
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: '**', component: NotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
