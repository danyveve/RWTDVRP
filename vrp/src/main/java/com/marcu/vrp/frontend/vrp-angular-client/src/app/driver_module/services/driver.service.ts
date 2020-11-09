import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {Driver} from "../model/driver";
import {HttpClient} from "@angular/common/http";
import {VrpInstance} from "../../vrp_module/model/vrp-instance";



@Injectable({
  providedIn: 'root'
})
export class DriverService {
  private driverUrl = 'http://localhost:8080/api/driver';

  constructor(private httpClient: HttpClient) { }

  getDrivers(): Observable<Driver[]> {
    return this.httpClient
      .get<Array<Driver>>(this.driverUrl + "/find/all");
  }

  getDriversFiltered(filter: string, sort: string, isAscending: boolean): Observable<Driver[]> {
    filter = filter.replace("+", "%2B");
    return this.httpClient
      .get<Array<Driver>>(this.driverUrl + "/find/filtered?filter=" + filter + "&sort=" + sort + "&isAscending=" + isAscending);
  }

  getDriver(id: number): Observable<Driver> {
    return this.httpClient
      .get<Driver>(this.driverUrl + "/find/" + id);
  }

  deleteById(id: number): Observable<void> {
    return this.httpClient
      .delete<void>(this.driverUrl + "/delete/" + id);
  }
}
