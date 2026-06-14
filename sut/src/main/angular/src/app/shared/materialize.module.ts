import { NgModule } from '@angular/core';
import { MaterializeDirective } from './materialize.directive';
import { PrimengCompatDirective } from './primeng-compat.directive';

@NgModule({
  declarations: [MaterializeDirective, PrimengCompatDirective],
  exports: [MaterializeDirective, PrimengCompatDirective]
})
export class MaterializeModule {}
