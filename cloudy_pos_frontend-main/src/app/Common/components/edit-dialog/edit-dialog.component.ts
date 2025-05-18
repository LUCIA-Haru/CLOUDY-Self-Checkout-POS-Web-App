import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FilterEditablePipe } from 'app/Common/pipe/filter-editable.pipe';

interface Column {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'number' | 'select' | 'date';
  options?: { value: string; label: string }[];
  required?: boolean;
  editable?: boolean;
}

interface DialogData {
  columns: Column[];
  row: any;
  mode: 'add' | 'edit';
  excludedFields?: string[];
  entityName?: string;
}

@Component({
  selector: 'app-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    FilterEditablePipe,
  ],
  templateUrl: './edit-dialog.component.html',
  styleUrls: ['./edit-dialog.component.css'],
})
export class EditDialogComponent {
  form: FormGroup;
  dialogTitle: string;
  excludedFields: string[];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {
    const entityName = this.data.entityName || 'Item';
    this.dialogTitle =
      this.data.mode === 'add' ? `Add New ${entityName}` : `Edit ${entityName}`;
    this.excludedFields = this.data.excludedFields || [];

    this.form = this.fb.group({});
    this.data.columns
      .filter(
        (col) =>
          col.editable !== false && !this.excludedFields.includes(col.key),
      )
      .forEach((col) => {
        const validators = col.required ? [Validators.required] : [];
        let defaultValue: any;

        if (this.data.mode === 'edit') {
          if (col.type === 'date' && this.data.row[col.key]) {
            // Normalize existing date to local midnight for display in datepicker
            defaultValue = this.normalizeDate(this.data.row[col.key]);
          } else if (col.type === 'select') {
            defaultValue = this.data.row[col.key]?.toString() || '';
          } else {
            defaultValue = this.data.row[col.key] || '';
          }
        } else {
          defaultValue =
            col.type === 'select' ? col.options?.[0]?.value || '' : '';
        }

        this.form.addControl(
          col.key,
          this.fb.control(defaultValue, validators),
        );
      });
  }

  // Normalize date to midnight in local timezone (for datepicker display)
  private normalizeDate(date: Date | string): Date {
    const d = new Date(date);
    return new Date(d.getFullYear(), d.getMonth(), d.getDate());
  }

  // Format date as YYYY-MM-DD string
  private formatDate(date: Date | string): string {
    const d = new Date(date);
    return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.form.valid) {
      const formValue = { ...this.form.value };

      // Format date fields as YYYY-MM-DD strings
      this.data.columns
        .filter((col) => col.type === 'date')
        .forEach((col) => {
          if (formValue[col.key]) {
            formValue[col.key] = this.formatDate(formValue[col.key]);
          }
        });

      const result =
        this.data.mode === 'edit'
          ? {
              ...this.data.row,
              ...formValue,
            }
          : formValue;

      if (this.data.mode === 'edit') {
        // Preserve the ID field
        const idKey = Object.keys(this.data.row).find((key) =>
          key.toLowerCase().includes('id'),
        );
        if (idKey) {
          result[idKey] = this.data.row[idKey];
        }
      }

      this.dialogRef.close(result);
    }
  }
}
