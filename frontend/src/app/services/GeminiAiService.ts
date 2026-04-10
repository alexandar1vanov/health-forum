import {Injectable} from '@angular/core';
import {catchError, from, map, Observable, of} from 'rxjs';
import {GoogleGenAI} from '@google/genai';
import {envirnment} from '../../environments/environment';

export interface ContentReviewResult {
  isSpam: boolean;
  isRelevant: boolean;
  reason?: string;
}

@Injectable({
  providedIn: 'root'
})
export class GeminiAiService {

  private client: any;
  private modelName: string = 'gemini-3-flash-preview';
  private forumContext: string = 'health forum';

  constructor() {
    this.client = new GoogleGenAI({
      apiKey: envirnment.geminiApiKey
    });
  }

  reviewContent(content: string, postContext?: string, title?: string): Observable<ContentReviewResult> {
    return from(this.runContentReview(content, postContext, title)).pipe(
      catchError(error => {
        console.error('Error in content review:', error);
        return of({isSpam: false, isRelevant: true});
      })
    );
  }

  reviewForumPost(title: string, content: string, postContext?: string): Observable<ContentReviewResult> {
    const combinedContent = `Title: ${title}\n\nContent: ${content}`;
    return this.reviewContent(combinedContent, postContext);
  }

  detectSpam(text: string): Observable<boolean> {
    return this.reviewContent(text).pipe(
      map(response => response.isSpam)
    );
  }

  setForumContext(context: string): void {
    this.forumContext = context;
  }

  private async runContentReview(text: string, postContext?: string, title?: string): Promise<ContentReviewResult> {
    try {
      const contextInfo = postContext || this.forumContext;
      let contentToAnalyze = text;
      if (title) {
        contentToAnalyze = `Title: ${title}\n\nContent: ${text}`;
      }

      const prompt = `
        You are a strict content moderator for a ${contextInfo}. Analyze the following comment for quality, appropriateness, and relevance.

        FORUM CONTEXT: ${contextInfo}

        COMMENT TO ANALYZE: "${contentToAnalyze}"

        IMPORTANT GUIDELINES FOR COMMENT EVALUATION:
        1. SPAM AND INAPPROPRIATE CONTENT:
           - ANY offensive language, profanity, obscenity, sexual content, hate speech in EITHER the title OR content = SPAM
           - Promotional content, unrelated links, excessive special characters, or nonsensical text = SPAM
           - If EITHER the title OR the content contains inappropriate language, the ENTIRE post must be flagged as spam
           - Be especially strict with titles - they must be appropriate and respectful
           - Even if the content portion is relevant and appropriate, if the title is inappropriate, the entire post is spam

        2. RELEVANCE:
           - Allow positive comments like "great post", "thank you", "good information", etc.
           - Allow any health-related comments, even brief ones like "my stomach hurts too"
           - Allow follow-up questions and personal experiences
           - Allow informative comments that add value to the discussion, such as sharing relevant experiences, providing helpful tips, or offering constructive insights related to the health topic.
           - Only flag as irrelevant if it's clearly unrelated to health topics or obviously out of place

        Make sure that your JSON response has isSpam=false for any health-related comments or questions that don't contain inappropriate language.

        Return your analysis in the following JSON format only:
        {
          "isSpam": boolean,
          "isRelevant": boolean,
          "reason": "Brief explanation of your decision"
        }

        CRITICAL: Any post with offensive language in EITHER title or content MUST have isSpam = true
      `;

      const response = await this.client.models.generateContent({
        model: this.modelName,
        contents: [{ role: 'user', parts: [{ text: `${this.forumContext}: ${prompt}` }] }]
      });
      const responseText = response.text.trim();
      console.log('Raw Gemini response:', responseText);

      try {
        let cleanedResponse = responseText;

        if (responseText.startsWith('```json')) {
          cleanedResponse = responseText.replace(/```json|```/g, '').trim();
        } else if (responseText.includes('```')) {
          cleanedResponse = responseText.replace(/```/g, '').trim();
        }

        const parsedResponse = JSON.parse(cleanedResponse);
        console.log('Parsed response:', parsedResponse);

        const lowerReason = (parsedResponse.reason || '').toLowerCase();
        const positiveIndicators = [
          'relevant to health',
          'health concern',
          'health-related',
          'no inappropriate',
          'appropriate',
          'not spam',
          'allowed'
        ];

        if (parsedResponse.isSpam &&
          positiveIndicators.some(term => lowerReason.includes(term)) &&
          !lowerReason.includes("inappropriate") &&
          !lowerReason.includes("offensive")) {
          console.log('Overriding spam detection to false based on positive reason content');
          parsedResponse.isSpam = false;
        }

        const offensiveTerms = ['contains profanity', 'contains obscene', 'contains vulgar',
          'contains explicit', 'has offensive', 'uses hate speech'];
        if (!parsedResponse.isSpam && offensiveTerms.some(term => lowerReason.includes(term))) {
          console.log('Overriding to spam based on offensive content indicators');
          parsedResponse.isSpam = true;
        }
        return {
          isSpam: !!parsedResponse.isSpam,
          isRelevant: !!parsedResponse.isRelevant,
          reason: parsedResponse.reason || 'No reason provided'
        };
      } catch (parseError) {
        console.warn('Failed to parse AI response:', responseText, parseError);

        const lowerResponse = responseText.toLowerCase();

        const spamIndicators = ['offensive content', 'inappropriate language', 'contains profanity'];
        const isLikelySpam = spamIndicators.some(term => lowerResponse.includes(term));

        return {
          isSpam: isLikelySpam,
          isRelevant: true,
          reason: isLikelySpam ? 'Potentially inappropriate content detected' : 'No issues detected'
        };
      }
    } catch (error) {
      console.error('Error in content review:', error);
      throw error;
    }
  }
}
