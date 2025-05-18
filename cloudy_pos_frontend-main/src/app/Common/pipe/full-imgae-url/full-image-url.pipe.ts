import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fullImageUrl',
})
export class FullImageUrlPipe implements PipeTransform {
  private baseUrl = 'http://localhost:8080'; // Only the domain and port

  transform(url: string): string {
    if (!url) return '';
    // console.log('Transforming image URL:', url);
    return url.startsWith('http') ? url : `${this.baseUrl}${url}`;
  }
}
