import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormsModule,
  FormControl,
  AbstractControl,
} from '@angular/forms';
import { Editor, Toolbar, NgxEditorMenuComponent, NgxEditorComponent, toHTML } from 'ngx-editor';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-additional-information',
  imports: [FormsModule, ReactiveFormsModule, NgxEditorMenuComponent, NgxEditorComponent],
  templateUrl: './additional-information.html',
  styleUrl: './additional-information.css',
})
export class AdditionalInformation implements OnInit, OnDestroy {
  @Input() parentForm!: FormGroup;
  @Output() notesHtmlChange = new EventEmitter<any | null>();
  @Output() specialTermsHtmlChange = new EventEmitter<any | null>();

  public editorNotes!: Editor;
  public editorSpecialTerms!: Editor;
  public toolbar: Toolbar = [
    ['bold', 'italic'],
    ['underline'],
    ['ordered_list', 'bullet_list'],
    ['text_color'],
  ];

  private notesSub?: Subscription | null;
  private specialTermsSub?: Subscription | null;

  ngOnInit(): void {
    this.editorNotes = new Editor();
    this.editorSpecialTerms = new Editor();

    this.notesSub = this.parseToHtml(this.notes, this.editorNotes, this.notesHtmlChange);
    this.specialTermsSub = this.parseToHtml(
      this.specialTerms,
      this.editorSpecialTerms,
      this.specialTermsHtmlChange,
    );
  }

  ngOnDestroy(): void {
    this.editorNotes.destroy();
    this.editorSpecialTerms.destroy();
    this.notesSub?.unsubscribe();
    this.specialTermsSub?.unsubscribe();
  }

  public get notes(): FormControl {
    return this.parentForm.get('notes') as FormControl;
  }

  public get specialTerms(): FormControl {
    return this.parentForm.get('specialTerms') as FormControl;
  }

  public onClickFocusEditor(editor: Editor): void {
    editor.commands.focus();
  }

  private parseToHtml(
    control: AbstractControl<any, any> | null,
    editor: Editor,
    event: EventEmitter<string | null>,
  ): Subscription | null {
    if (!control) return null;

    if (control.value) {
      const html = toHTML(control.value, editor.schema);
      event.emit(html);
    }

    return control.valueChanges.subscribe((value) => {
      const html = value ? toHTML(value, editor.schema) : null;
      event.emit(html);
    });
  }
}
