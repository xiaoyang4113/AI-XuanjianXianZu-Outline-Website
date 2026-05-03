export interface Project {
  id: number;
  name: string;
  author: string;
  description: string;
  coverUrl: string;
  genre: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}
export interface ProjectStats {
  projectId: number;
  projectName: string;
  totalItems: number;
  totalCategories: number;
  totalTags: number;
  goldenNaturesCount: number;
  categoryCounts: Record<string, number>;
}
export interface TagItem {
  id: number;
  name: string;
  substitute: boolean;
}
export interface SubstituteNote {
  source: string;
  target: string;
}

export interface ItemBrief {
  id: number;
  name: string;
  badge: string;
  goldenNature: string | null;
  notes: string | null;
  tags: TagItem[];
  substituteNotes: SubstituteNote[];
  isEmptyCertification?: boolean;
}
export interface ItemDetail {
  id: number;
  name: string;
  badge: string;
  goldenNature: string | null;
  notes: string | null;
  categoryName: string | null;
  subCategoryName: string | null;
  tags: TagItem[];
  substituteNotes: SubstituteNote[];
  isEmptyCertification?: boolean;
}
export interface CategoryWithItems {
  id: number;
  name: string;
  element: string;
  itemCount: number;
  subCategories: SubCategoryWithItems[];
  items: ItemBrief[];
}
export interface SubCategoryWithItems {
  id: number;
  name: string;
  items: ItemBrief[];
}
export interface SearchResult {
  id: number;
  name: string;
  badge: string;
  goldenNature: string | null;
  tags: TagItem[];
  type: string;           // ← 原来叫 matchType，已改为 type
}
