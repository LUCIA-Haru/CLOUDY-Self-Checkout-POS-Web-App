import { Pipe, PipeTransform } from '@angular/core';

interface Column {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'number' | 'select' | 'date';
  options?: { value: string; label: string }[];
  required?: boolean;
  editable?: boolean;
}

@Pipe({
  name: 'filterEditable',
})
export class FilterEditablePipe implements PipeTransform {
  transform(columns: Column[], excludedFields: string[]): Column[] {
    return columns.filter(
      (col) => col.editable !== false && !excludedFields.includes(col.key),
    );
  }
}
