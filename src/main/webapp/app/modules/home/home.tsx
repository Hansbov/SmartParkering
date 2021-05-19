import './home.scss';

import React, { useEffect, useState } from 'react';
import { RouteComponentProps } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { connect } from 'react-redux';
import {  Row, Col, Button } from 'reactstrap';
import { IRootState } from 'app/shared/reducers';
import { getEntities, updateEntity, getEntity } from 'app/entities/parking-spot/parking-spot.reducer';
import { getEntities as getCarparks} from 'app/entities/car-park/car-park.reducer';

export interface IHomeProp extends StateProps, DispatchProps, RouteComponentProps<{ url: string}> {}

export const Home = (props: IHomeProp) => {
  const { parkingSpotList, loadingParkingSpot, loadingCarPark, account, carParkList } = props;

  const initialSelectedCarPark = {id: 1, name: "Kista Big Carpark"};

  const [ selectedCarPark, setSelectedCarPark] = useState(initialSelectedCarPark);

  useEffect(() => {
    props.getCarparks();
  }, []);

  useEffect(() => {
    props.getEntities();
  }, [parkingSpotList]);

  return (
    <div>
      <Col md="9" className="parkingLot"/>
        <h2>
          <Translate contentKey="home.title">Welcome SmartParking!</Translate>
        </h2>
          {carParkList && carParkList.length > 0 ? (
            carParkList.map((carPark) => (
              <Button key={carPark.id} onClick={() => setSelectedCarPark(carPark)}>{carPark.name}</Button>
            ))
          ) : !loadingCarPark && (
            <div className="alert alert-warning">
              <Translate contentKey="smartParkingApp.carPark.home.notFound">No Carpark found</Translate>
            </div>
          )}
        {account && account.login ? (
          <div className="parkingLot">
            <div>
              {carParkList && carParkList.length > 0 && selectedCarPark ? (
                <div key={selectedCarPark.id}>
                  <Row> {selectedCarPark.name}</Row>
                    {parkingSpotList && parkingSpotList.length > 0 ? (
                      parkingSpotList.map((parkingSpot, j) => (
                        (parkingSpot.carPark != null && parkingSpot.carPark.id === selectedCarPark.id) ?
                          (parkingSpot.available ? <Col key={parkingSpot.id} className = "green-hspot"/> : <Col key={parkingSpot.id} className = "red-hspot"/>) : ('')
                      ))
                    ) : !loadingParkingSpot && (
                      <div className="alert alert-warning">
                        <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
                      </div>
                    )}
                </div>
              ) : (!loadingCarPark && (
                <div className="alert alert-warning">
                  <Translate contentKey="smartParkingApp.carPark.home.notFound">No Carpark found</Translate>
                </div>
              ))}
            </div>
          </div> 
          ) : (
          <div className="parkingLot">
            <div>
              {carParkList && carParkList.length > 0  && selectedCarPark ? (
                <div key={selectedCarPark.id}>
                  <Row> {selectedCarPark.name}</Row>
                    {parkingSpotList && parkingSpotList.length > 0 ? (
                      parkingSpotList.map((parkingSpot) => (
                        (parkingSpot.carPark != null && parkingSpot.carPark.id === selectedCarPark.id ) ?
                          (parkingSpot.available ? <Col key={parkingSpot.id} className = "green-hspot"/> : <Col key={parkingSpot.id} className = "red-hspot"/>) : ('')
                      ))
                    ) : !loadingParkingSpot && (
                      <div className="alert alert-warning">
                        <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
                      </div>
                    )}
                  </div>
                ) : !loadingCarPark && (
                  <div className="alert alert-warning">
                    <Translate contentKey="smartParkingApp.carPark.home.notFound">No Carpark found</Translate>
                  </div>
                )}
            </div>
          </div>
        )}
    </div>
  );
};

const mapStateToProps = (storeState, {carPark,parkingSpot}: IRootState) => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  parkingSpotList: storeState.parkingSpot.entities,
  loadingParkingSpot: storeState.parkingSpot.loading,
  loadingCarPark: storeState.carPark.loading,
  carParkList: storeState.carPark.entities,
});

const mapDispatchToProps = {
  getEntities,
  updateEntity,
  getCarparks
};
type DispatchProps = typeof mapDispatchToProps;
type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps, mapDispatchToProps)(Home);
