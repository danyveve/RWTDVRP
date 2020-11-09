import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {VrpService} from "../services/vrp.service";
import {VrpInstance} from "../model/vrp-instance";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {switchMap} from "rxjs/operators";

@Component({
  selector: 'app-vrp-details',
  templateUrl: './vrp-details.component.html',
  styleUrls: ['./vrp-details.component.scss']
})
export class VrpDetailsComponent implements OnInit {
  vrpInstance: VrpInstance;

  constructor(
    private location: Location,
    private vrpService: VrpService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.params
      .pipe(switchMap((params: Params) => this.vrpService.getVrp(+params['id'])))
      .subscribe(vrp => this.vrpInstance = vrp);
  }

  goBack() {
    this.location.back()
  }

  stopVrpInstance(id: number) {
    this.vrpService.stopVrp(id).subscribe(vrp => this.vrpInstance = vrp);
  }

  secondsToString(seconds: number): string {
    let hours = Math.floor(seconds / 3600);
    seconds = seconds - hours * 3600;
    let minutes = Math.floor(seconds / 60);
    seconds = seconds - minutes * 60;
    return hours + "h " + minutes + "m " + seconds + "s";
  }

  deleteVrp(id: number) {
    this.vrpService.deleteVrp(id)
      .subscribe(
        (result) => this.router.navigateByUrl("/vrp")
      )
  }
}
