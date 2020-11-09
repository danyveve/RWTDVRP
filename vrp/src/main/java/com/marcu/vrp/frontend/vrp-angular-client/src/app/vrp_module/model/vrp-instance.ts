import {Route} from "./route";
import {GeographicPoint} from "./geographic-point";
import {DeliveryPoint} from "./delivery-point";

export class VrpInstance {
  id: number;
  userId: number;
  createdOn: Date;
  depot: GeographicPoint;
  preferredDepartureTime: Date;
  suggestedDepartureTime: Date;
  totalCost: number;
  deliveryPoints: Array<DeliveryPoint>;
  routes: Array<Route>;
}
