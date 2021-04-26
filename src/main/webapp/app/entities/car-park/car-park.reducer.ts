import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ICarPark, defaultValue } from 'app/shared/model/car-park.model';

export const ACTION_TYPES = {
  SEARCH_CARPARKS: 'carPark/SEARCH_CARPARKS',
  FETCH_CARPARK_LIST: 'carPark/FETCH_CARPARK_LIST',
  FETCH_CARPARK: 'carPark/FETCH_CARPARK',
  CREATE_CARPARK: 'carPark/CREATE_CARPARK',
  UPDATE_CARPARK: 'carPark/UPDATE_CARPARK',
  PARTIAL_UPDATE_CARPARK: 'carPark/PARTIAL_UPDATE_CARPARK',
  DELETE_CARPARK: 'carPark/DELETE_CARPARK',
  RESET: 'carPark/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICarPark>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type CarParkState = Readonly<typeof initialState>;

// Reducer

export default (state: CarParkState = initialState, action): CarParkState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CARPARKS):
    case REQUEST(ACTION_TYPES.FETCH_CARPARK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CARPARK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_CARPARK):
    case REQUEST(ACTION_TYPES.UPDATE_CARPARK):
    case REQUEST(ACTION_TYPES.DELETE_CARPARK):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_CARPARK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_CARPARKS):
    case FAILURE(ACTION_TYPES.FETCH_CARPARK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CARPARK):
    case FAILURE(ACTION_TYPES.CREATE_CARPARK):
    case FAILURE(ACTION_TYPES.UPDATE_CARPARK):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_CARPARK):
    case FAILURE(ACTION_TYPES.DELETE_CARPARK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CARPARKS):
    case SUCCESS(ACTION_TYPES.FETCH_CARPARK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_CARPARK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_CARPARK):
    case SUCCESS(ACTION_TYPES.UPDATE_CARPARK):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_CARPARK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_CARPARK):
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

const apiUrl = 'api/car-parks';
const apiSearchUrl = 'api/_search/car-parks';

// Actions

export const getSearchEntities: ICrudSearchAction<ICarPark> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CARPARKS,
  payload: axios.get<ICarPark>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<ICarPark> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_CARPARK_LIST,
  payload: axios.get<ICarPark>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<ICarPark> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CARPARK,
    payload: axios.get<ICarPark>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ICarPark> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CARPARK,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ICarPark> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CARPARK,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<ICarPark> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_CARPARK,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICarPark> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CARPARK,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
