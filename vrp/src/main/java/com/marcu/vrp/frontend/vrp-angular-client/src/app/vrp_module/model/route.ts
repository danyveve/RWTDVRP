import {Driver} from "../../driver_module/model/driver";
import {VrpInstance} from "./vrp-instance";
import {GeographicPointToRouteAssignment} from "./geographic-point-to-route-assignment";

export class Route {
  driver: Driver;
  vrpInstance: VrpInstance;
  cost: number;
  geographicPointToRouteAssignments: Array<GeographicPointToRouteAssignment>;
}
