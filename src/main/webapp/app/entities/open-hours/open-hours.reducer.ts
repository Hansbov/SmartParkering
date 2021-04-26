import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IOpenHours, defaultValue } from 'app/shared/model/open-hours.model';

export const ACTION_TYPES = {
  SEARCH_OPENHOURS: 'openHours/SEARCH_OPENHOURS',
  FETCH_OPENHOURS_LIST: 'openHours/FETCH_OPENHOURS_LIST',
  FETCH_OPENHOURS: 'openHours/FETCH_OPENHOURS',
  CREATE_OPENHOURS: 'openHours/CREATE_OPENHOURS',
  UPDATE_OPENHOURS: 'openHours/UPDATE_OPENHOURS',
  PARTIAL_UPDATE_OPENHOURS: 'openHours/PARTIAL_UPDATE_OPENHOURS',
  DELETE_OPENHOURS: 'openHours/DELETE_OPENHOURS',
  RESET: 'openHours/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IOpenHours>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type OpenHoursState = Readonly<typeof initialState>;

// Reducer

export default (state: OpenHoursState = initialState, action): OpenHoursState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_OPENHOURS):
    case REQUEST(ACTION_TYPES.FETCH_OPENHOURS_LIST):
    case REQUEST(ACTION_TYPES.FETCH_OPENHOURS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_OPENHOURS):
    case REQUEST(ACTION_TYPES.UPDATE_OPENHOURS):
    case REQUEST(ACTION_TYPES.DELETE_OPENHOURS):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_OPENHOURS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_OPENHOURS):
    case FAILURE(ACTION_TYPES.FETCH_OPENHOURS_LIST):
    case FAILURE(ACTION_TYPES.FETCH_OPENHOURS):
    case FAILURE(ACTION_TYPES.CREATE_OPENHOURS):
    case FAILURE(ACTION_TYPES.UPDATE_OPENHOURS):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_OPENHOURS):
    case FAILURE(ACTION_TYPES.DELETE_OPENHOURS):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_OPENHOURS):
    case SUCCESS(ACTION_TYPES.FETCH_OPENHOURS_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_OPENHOURS):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_OPENHOURS):
    case SUCCESS(ACTION_TYPES.UPDATE_OPENHOURS):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_OPENHOURS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_OPENHOURS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/open-hours';
const apiSearchUrl = 'api/_search/open-hours';

// Actions

export const getSearchEntities: ICrudSearchAction<IOpenHours> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_OPENHOURS,
  payload: axios.get<IOpenHours>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IOpenHours> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_OPENHOURS_LIST,
  payload: axios.get<IOpenHours>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IOpenHours> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_OPENHOURS,
    payload: axios.get<IOpenHours>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IOpenHours> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_OPENHOURS,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IOpenHours> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_OPENHOURS,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IOpenHours> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_OPENHOURS,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IOpenHours> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_OPENHOURS,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
