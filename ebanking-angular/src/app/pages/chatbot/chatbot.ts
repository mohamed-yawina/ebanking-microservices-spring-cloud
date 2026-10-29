
import {
  Component,
  ChangeDetectorRef,
  ElementRef,
  ViewChild,
  inject
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { marked } from 'marked';
import DOMPurify from 'dompurify';
import { ChatbotService } from '../../services/chatbot';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.css'
})
export class Chatbot {

  private chatbotService = inject(ChatbotService);
  private cdr = inject(ChangeDetectorRef);
  private sanitizer = inject(DomSanitizer);

  @ViewChild('messagesContainer')
  messagesContainer?: ElementRef<HTMLDivElement>;

  userMessage = '';
  loading = false;
  error = '';

  messages: ChatMessage[] = [
    {
      role: 'assistant',
      content:
        'Bonjour ! Je suis votre assistant bancaire IA. ' +
        'Comment puis-je vous aider ?',
      timestamp: new Date()
    }
  ];

  suggestions: string[] = [
    'Affiche tous les clients bancaires',
    'Combien de comptes bancaires existent ?',
    'Affiche les comptes du client 1',
    'Donne les informations du client 2'
  ];

  formatMessage(content: string): SafeHtml {
    const html = marked.parse(content, {
      async: false,
      breaks: true,
      gfm: true
    }) as string;

    const cleanHtml = DOMPurify.sanitize(html);

    return this.sanitizer.bypassSecurityTrustHtml(cleanHtml);
  }

  sendMessage(): void {
    const question = this.userMessage.trim();

    if (!question || this.loading) {
      return;
    }

    this.messages.push({
      role: 'user',
      content: question,
      timestamp: new Date()
    });

    this.userMessage = '';
    this.loading = true;
    this.error = '';

    this.cdr.detectChanges();
    this.scrollToBottom();

    this.chatbotService.sendMessage(question).subscribe({
      next: (response: string) => {

        this.messages.push({
          role: 'assistant',
          content: response || 'Aucune réponse reçue.',
          timestamp: new Date()
        });

        this.loading = false;
        this.cdr.detectChanges();
        this.scrollToBottom();
      },

      error: (err: unknown) => {
        console.error('Erreur chatbot :', err);

        this.error =
          "Impossible de contacter l'assistant IA.";

        this.messages.push({
          role: 'assistant',
          content:
            "Désolé, une erreur s'est produite. " +
            "Veuillez réessayer.",
          timestamp: new Date()
        });

        this.loading = false;
        this.cdr.detectChanges();
        this.scrollToBottom();
      }
    });
  }

  useSuggestion(suggestion: string): void {
    if (this.loading) {
      return;
    }

    this.userMessage = suggestion;
    this.sendMessage();
  }

  clearChat(): void {
    if (this.loading) {
      return;
    }

    this.messages = [
      {
        role: 'assistant',
        content:
          'Nouvelle conversation. Comment puis-je vous aider ?',
        timestamp: new Date()
      }
    ];

    this.userMessage = '';
    this.error = '';

    this.cdr.detectChanges();
  }

  private scrollToBottom(): void {
    requestAnimationFrame(() => {
      const element = this.messagesContainer?.nativeElement;

      if (element) {
        element.scrollTop = element.scrollHeight;
      }
    });
  }
}
