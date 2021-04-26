import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IParkingSpot, defaultValue } from 'app/shared/model/parking-spot.model';

export const ACTION_TYPES = {
  SEARCH_PARKINGSPOTS: 'parkingSpot/SEARCH_PARKINGSPOTS',
  FETCH_PARKINGSPOT_LIST: 'parkingSpot/FETCH_PARKINGSPOT_LIST',
  FETCH_PARKINGSPOT: 'parkingSpot/FETCH_PARKINGSPOT',
  CREATE_PARKINGSPOT: 'parkingSpot/CREATE_PARKINGSPOT',
  UPDATE_PARKINGSPOT: 'parkingSpot/UPDATE_PARKINGSPOT',
  PARTIAL_UPDATE_PARKINGSPOT: 'parkingSpot/PARTIAL_UPDATE_PARKINGSPOT',
  DELETE_PARKINGSPOT: 'parkingSpot/DELETE_PARKINGSPOT',
  RESET: 'parkingSpot/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IParkingSpot>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ParkingSpotState = Readonly<typeof initialState>;

// Reducer

export default (state: ParkingSpotState = initialState, action): ParkingSpotState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PARKINGSPOTS):
    case REQUEST(ACTION_TYPES.FETCH_PARKINGSPOT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PARKINGSPOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_PARKINGSPOT):
    case REQUEST(ACTION_TYPES.UPDATE_PARKINGSPOT):
    case REQUEST(ACTION_TYPES.DELETE_PARKINGSPOT):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_PARKINGSPOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_PARKINGSPOTS):
    case FAILURE(ACTION_TYPES.FETCH_PARKINGSPOT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PARKINGSPOT):
    case FAILURE(ACTION_TYPES.CREATE_PARKINGSPOT):
    case FAILURE(ACTION_TYPES.UPDATE_PARKINGSPOT):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_PARKINGSPOT):
    case FAILURE(ACTION_TYPES.DELETE_PARKINGSPOT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PARKINGSPOTS):
    case SUCCESS(ACTION_TYPES.FETCH_PARKINGSPOT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PARKINGSPOT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_PARKINGSPOT):
    case SUCCESS(ACTION_TYPES.UPDATE_PARKINGSPOT):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_PARKINGSPOT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_PARKINGSPOT):
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

const apiUrl = 'api/parking-spots';
const apiSearchUrl = 'api/_search/parking-spots';

// Actions

export const getSearchEntities: ICrudSearchAction<IParkingSpot> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PARKINGSPOTS,
  payload: axios.get<IParkingSpot>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IParkingSpot> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_PARKINGSPOT_LIST,
  payload: axios.get<IParkingSpot>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IParkingSpot> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PARKINGSPOT,
    payload: axios.get<IParkingSpot>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IParkingSpot> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PARKINGSPOT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IParkingSpot> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PARKINGSPOT,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IParkingSpot> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_PARKINGSPOT,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IParkingSpot> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PARKINGSPOT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
