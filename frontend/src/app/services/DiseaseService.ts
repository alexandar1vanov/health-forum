import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DiseaseResponse} from '../models/DiseaseResponse';

@Injectable({
  providedIn: 'root'
})
export class DiseaseService {

  private readonly apiDiseasesUrl = '/api/diseases'

  http = inject(HttpClient)

  getAllDiseases() {
    return this.http.get<DiseaseResponse[]>(this.apiDiseasesUrl)
  }

}
