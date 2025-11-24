import { DiseaseCategory } from "./enums/DiseaseCategory";

export interface Disease {
  id: number;
  name: string;
  category: DiseaseCategory;
  description: string;
}
