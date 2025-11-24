import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { AdminUserUpdateDTO } from '../../models/AdminUserUpdateDTO';
import {User} from '../../models/User';

@Component({
  selector: 'app-user-update',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './user-update.component.html',
  styleUrl: './user-update.component.css'
})
export class UserUpdateComponent implements OnInit {
  @Input() user!: User;
  @Output() save = new EventEmitter<AdminUserUpdateDTO>();
  @Output() cancel = new EventEmitter<void>();

  editForm: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder) {
    this.editForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.user) {
      this.editForm.patchValue({
        email: this.user.email,
        role: this.user.role
      });
    }
  }

  onSave(): void {
    this.submitted = true;

    if (this.editForm.valid) {
      const updateDto: AdminUserUpdateDTO = {
        email: this.editForm.value.email,
        role: this.editForm.value.role
      };
      this.save.emit(updateDto);
    }
  }

  onCancel(): void {
    this.cancel.emit();
    this.submitted = false;
  }

  get emailControl() { return this.editForm.get('email'); }

  hasError(controlName: string): boolean {
    const control = this.editForm.get(controlName);
    return (control?.invalid && (control?.dirty || control?.touched || this.submitted)) ?? false;
  }
}
