import { NgModule } from '@angular/core';
import { MaterializeDirective } from './materialize.directive';
import { NativeDialogDirective } from './native-dialog.directive';
import { PrimengCompatDirective } from './primeng-compat.directive';

@NgModule({
  declarations: [MaterializeDirective, NativeDialogDirective, PrimengCompatDirective],
  exports: [MaterializeDirective, NativeDialogDirective, PrimengCompatDirective]
})
export class MaterializeModule {}
