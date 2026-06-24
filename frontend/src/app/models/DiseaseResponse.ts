import { DiseaseCategory } from "./enums/DiseaseCategory";

export interface DiseaseResponse {
  id: number;
  name: string;
  category: DiseaseCategory;
  description: string;
}
