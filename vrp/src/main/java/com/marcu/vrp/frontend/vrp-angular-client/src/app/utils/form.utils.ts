import {AbstractControl, FormGroup, ValidatorFn} from '@angular/forms';

// custom validator to check that two fields match
export function MustMatch(controlName: string, matchingControlName: string) {
  return (abstractControl: AbstractControl) => {
    const control = abstractControl.get(controlName);
    const matchingControl = abstractControl.get(matchingControlName);

    if (matchingControl.errors && !matchingControl.errors.mustMatch) {
      // return if another validator has already found an error on the matchingControl
      removeErrors(['mustMatch'], matchingControl);
      return null;
    }

    // set error on matchingControl if validation fails
    if (control.value !== matchingControl.value) {
      setErrors({mustMatch: true}, matchingControl);
      return {'mustMatch' : {value: matchingControl.value}};
    } else {
      removeErrors(['mustMatch'], matchingControl);
      return null;
    }
  }
}

function setErrors(error: {[key: string]: any }, control: AbstractControl) {
  control.setErrors({...control.errors, ...error});
}
function  removeErrors(keys: string[], control: AbstractControl) {
  const remainingErrors = keys.reduce((errors, key) => {
    delete  errors[key];
    return errors;
  }, {...control.errors});
  control.setErrors(Object.keys(remainingErrors).length > 0 ? remainingErrors : null);
}
