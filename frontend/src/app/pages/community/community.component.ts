import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TruncatePipe } from '../../shared/pipes/truncate.pipe';
import { MatDialog } from '@angular/material/dialog';
import { NewPostComponent } from './new-post/new-post.component';
import { CommentService } from '../../services/comment.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, TruncatePipe],
  templateUrl: './community.component.html',
  styleUrls: ['./community.component.css']
})
export class CommunityComponent implements OnInit {
  selectedCategory: string | null = null;
  categories: string[] = ['Todos los temas', 'Ansiedad', 'Depresión', 'Estrés', 'Meditación'];
  posts: any[] = [];

  constructor(private readonly router: Router,private readonly commentService: CommentService, private readonly dialog: MatDialog) {}

  ngOnInit(): void {
    this.commentService.getComments().subscribe({
      next: (data) => {
        this.posts = data;
      },
      error: (error) => {
        console.error('Error al cargar los comentarios:', error);
      }
    });
  }

  // Filtra los posts 
  private normalizeCategory(category: string): string {
    return category
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .trim();
  }

  get filteredPosts() {
    if (!this.selectedCategory) return this.posts;
    
    return this.posts.filter(post => 
      this.normalizeCategory(post.tema) === this.normalizeCategory(this.selectedCategory!)
    );
  }

  setFilter(category: string) {
    this.selectedCategory = category === 'Todos los temas' ? null : category;
  }

  trackByPostId(index: number, post: any): number {
    return post.id;
  }
  

  openNewPostDialog(): void {
    const dialogRef = this.dialog.open(NewPostComponent, {
      width: '600px',
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Nuevo post:', result);
      }
      this.commentService.getComments().subscribe({
        next: (data) => {
          this.posts = data;
        },
        error: (error) => {
          console.error('Error al cargar los comentarios:', error);
        }
      });
    });
  }
  goToHome() {
    this.router.navigate(['/']);
  }
}