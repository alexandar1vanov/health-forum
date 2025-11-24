import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Disease} from '../models/Disease';

@Injectable({
  providedIn: 'root'
})
export class DiseaseService {

  private readonly apiDiseasesUrl = 'http://localhost:8080/api/diseases'

  http = inject(HttpClient)

  getAllDiseases() {
    return this.http.get<Disease[]>(this.apiDiseasesUrl)
  }

}
