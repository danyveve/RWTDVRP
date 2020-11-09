import {GeographicPoint} from "./geographic-point";
import {Driver} from "../../driver_module/model/driver";

export class StartVrpRequest {
  depot: GeographicPoint;
  deliveryPoints: Array<GeographicPoint>;
  drivers: Array<Driver>;
  preferredDepartureTime: string;
}
