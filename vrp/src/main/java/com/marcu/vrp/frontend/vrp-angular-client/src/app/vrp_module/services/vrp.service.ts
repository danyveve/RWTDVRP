import {Injectable} from '@angular/core';
import {StartVrpRequest} from "../model/start-vrp-request";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Driver} from "../../driver_module/model/driver";
import {VrpInstance} from "../model/vrp-instance";

@Injectable({
  providedIn: 'root'
})
export class VrpService {
  private vrpLink = "http://localhost:8080/api/vrp";

  constructor(
    private httpClient: HttpClient
  ) {
  }

  startVrp(vrpInstance: StartVrpRequest): Observable<number> {
    return this.httpClient.post<number>(this.vrpLink + "/start", vrpInstance);
  }

  getVrps(): Observable<VrpInstance[]> {
    return this.httpClient
      .get<Array<VrpInstance>>(this.vrpLink + "/find/all");
  }

  getVrpsFiltered(filter: string, sort: string, isAscending: boolean): Observable<VrpInstance[]> {
    filter = filter.replace("+", "%2B");
    return this.httpClient
      .get<Array<VrpInstance>>(this.vrpLink + "/find/filtered?filter=" + filter + "&sort=" + sort + "&isAscending=" + isAscending);
  }

  getVrp(id: number): Observable<VrpInstance> {
    return this.httpClient.get<VrpInstance>(this.vrpLink + "/get/" + id);
  }

  stopVrp(id: number): Observable<VrpInstance> {
    return this.httpClient.get<VrpInstance>(this.vrpLink + "/stop/" + id);
  }

  deleteVrp(id: number): Observable<void> {
    return this.httpClient.delete<void>(this.vrpLink + "/delete/" + id);
  }
}
