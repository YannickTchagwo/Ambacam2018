import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GlobalVariablesService} from '../global-variables/global-variables.service';
import {Strings} from '../../utils/strings';
import {AppConstant} from '../../../appConstant';
import {Requete} from '../../models/requete/requete';

@Injectable()
export class RequeteService {

  constructor(private _http: HttpClient, private _authConstants: GlobalVariablesService) {
  }

  list() {
    return this._http.get(AppConstant.REQUETE_COLLECTION_PATH.toString(), this._authConstants.loadHeader());
  }

  create(requete: Requete, operateurId: number) {
    return this._http.post(Strings.format(AppConstant.REQUETE_CREATE_ITEM_PATH.toString(), operateurId, requete.requerantId), requete, this._authConstants.loadHeader());
  }

  get(requeteId: number) {
    return this._http.get(Strings.format(AppConstant.REQUETE_READ_ITEM_PATH, 0, 0, requeteId), this._authConstants.loadHeader());
  }

  update(requeteId: number, requete: Requete) {
    return this._http.put(Strings.format(AppConstant.REQUETE_READ_ITEM_PATH, 0, 0, requeteId), requete, this._authConstants.loadHeader());
  }

  updateStatut(requeteId: number, statusId: number) {
    const statusTO = {
      statusId: statusId
    };
    return this._http.put(Strings.format(AppConstant.REQUETE_UPDATE_STATUT_ITEM_PATH, 0, 0, requeteId), statusTO, this._authConstants.loadHeader());
  }

  delete(requeteId: number) {
    return this._http.delete(Strings.format(AppConstant.REQUETE_READ_ITEM_PATH, 0, 0, requeteId), this._authConstants.loadHeader());
  }

  updateStatusOfRequetes(requeteIds: any, requeteGroupeId: number, operateurId: number) {
    return this._http.put(Strings.format(AppConstant.REQUETE_UPDATE_STATUT_GROUPE_PATH, operateurId, requeteGroupeId), requeteIds, this._authConstants.loadHeader());
  }

}
