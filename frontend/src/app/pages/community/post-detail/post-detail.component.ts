import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommunityComponent } from '../community.component';

@Component({
  standalone: true,
  selector: 'app-post-detail',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css']
})
export class PostDetailComponent implements OnInit {
  postId!: number;
  post: any;
  comments: any[] = [];
  newComment: string = '';

  constructor(
    private route: ActivatedRoute,
    private communityComponent: CommunityComponent
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.postId = +params['id'];
      this.loadPost();
    });
  }

  private loadPost() {
    // Buscar el post en la lista del CommunityComponent
    const foundPost = this.communityComponent.posts.find(p => p.id === this.postId);

    if (foundPost) {
      this.post = foundPost;
      // Cargar comentarios (aquí deberías implementar un servicio real)
      this.comments = [
        { text: '¡Muy interesante!', user: 'juan' },
        { text: 'Gracias por compartir.', user: 'ana' }
      ];
    } else {
      // Redirigir si no encuentra el post
      window.location.href = '/community';
    }
  }

  addComment() {
    if (this.newComment.trim()) {
      this.comments.push({
        text: this.newComment,
        user: 'anonimo',
        date: new Date()
      });
      this.newComment = '';
    }
  }
}
