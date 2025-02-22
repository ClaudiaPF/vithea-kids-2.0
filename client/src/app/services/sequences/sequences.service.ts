import { Injectable } from '@angular/core';
import { HttpApiClient } from '../http/http-api-client.service';

@Injectable()
export class SequencesService {

  success = false
  failure = false;
  textSuccess;
  textFailure;

  constructor(public http: HttpApiClient) { }

  getSequences() {
    return this.http.get('/listsequences').map(result => result && result.json());
  }

  getSequence(sequenceId) {
    return this.http.get('/sequences/' + sequenceId).map(result => result && result.json());
  }

  registerSequence(sequence) {
    return this.http.post('/registersequence', sequence).map(result => result && result.json());
  }

  editSequence(sequence) {
    return this.http.post('/editsequence/' + sequence.sequenceId, sequence);
  }

  getSequenceChildren(sequenceId) {
    return this.http.get('/sequences/' + sequenceId + '/children').map(result => result && result.json());
  }

  deleteSequence(sequenceId) {
    return this.http.delete('/deletesequence/' + sequenceId);
  }

  getSuccess() {
    return this.success;
  }
  getFailure() {
    return this.failure;
  }
  getTextSuccess() {
    return this.textSuccess;
  }
  getTextFailure() {
    return this.textFailure;
  }
  setSuccess(success: boolean) {
    this.success = success;
  }
  setFailure(failure: boolean) {
    this.failure = failure;
  }
  setTextSuccess(text: string) {
    this.textSuccess = text;
  }
  setTextFailure(text: string) {
    this.textFailure = text;
  }
}
