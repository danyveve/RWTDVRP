import {InjectionToken} from "@angular/core";
import * as Rollbar from "rollbar";

const rollbarConfig = {
  accessToken: 'ff9bc65d18a84889b0cca2774032076e',
  captureUncaught: true,
  captureUnhandledRejections: true,
};

export function rollbarFactory() {
  return new Rollbar(rollbarConfig)
}

export const RollbarService = new InjectionToken<Rollbar>('rollbar');
