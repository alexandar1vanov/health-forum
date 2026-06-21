import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PostRatingResponse} from '../models/PostRatingResponse';
import {PostRatingRequest} from '../models/PostRatingRequest';

@Injectable({
  providedIn: 'root'
})
export class PostRatingService {
  private baseUrl = "/api/ratings"

  readonly #http = inject(HttpClient)

  getRatingsByActiveUser() {
    return this.#http.get<PostRatingResponse[]>(`${this.baseUrl}`);
  }

  getRatingByPostId(postId: number) {
    return this.#http.get<PostRatingResponse>(`${this.baseUrl}/${postId}`);
  }

  submitRating(postRatingRequest: PostRatingRequest) {
    return this.#http.post<PostRatingRequest>(`${this.baseUrl}`, postRatingRequest);
  }

  deleteRating(postId: number) {
    return this.#http.delete<void>(`${this.baseUrl}/${postId}`);
  }
}
