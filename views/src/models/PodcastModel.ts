import { UserSimple } from "./User";

export interface Podcast {
  id: string;
  title: string;
  content: string;
  thumbnailUrl: string | null;
  videoUrl: string;
  views: number;
  totalLikes: number;
  totalComments: number;
  username: string;
  createdDay: string;
  lastEdited: string;
  user: UserSimple;
  active: boolean;
  liked: boolean;
}

export interface PodcastResponse {
  podcasts: Podcast[];
  totalPages: number;
  currentPage: number;
}

