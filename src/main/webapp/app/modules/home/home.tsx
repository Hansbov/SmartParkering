import './home.scss';

import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { connect } from 'react-redux';
import { Container, Row, Col, Alert } from 'reactstrap';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities, updateEntity } from 'app/entities/parking-spot/parking-spot.reducer';
import { ICarPark } from 'app/shared/model/car-park.model';
import { getSearchEntities as getSearchCarpark, getEntities as getCarpark} from 'app/entities/car-park/car-park.reducer';

export interface IHomeProp extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Home = (props: IHomeProp) => {
  const { parkingSpotList, loading, load, account, carParkList } = props;

  const [search, setSearch] = useState('');
  const [searchCarpark, setSearchCarpark] = useState('');


  useEffect(() => {
    props.getCarpark();
  }, []);

  useEffect(() => {
  props.getEntities();
  }, [parkingSpotList]);


  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
    if(searchCarpark){
      props.getSearchCarpark(searchCarpark);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
    setSearchCarpark('');
    props.getCarpark();
  };

  const handleSearch = (event: { target: { value: React.SetStateAction<string>; }; }) => setSearch(event.target.value);

  const handleSyncList = () => {
    props.getEntities();
    props.getCarpark();
  };


  return (
    <div>
      <Col md="9" className="parkingLot"/>
        <h2>
          <Translate contentKey="home.title">Welcome SmartParking!</Translate>
        </h2>

      {account && account.login ? (
        <div className="parkingLot">
          <div>
            {carParkList && carParkList.length > 0 ? (
            carParkList.map((carPark, i) => (
            <div key={carPark.id}>
            <Row> {carPark.name}</Row>
            {parkingSpotList && parkingSpotList.length > 0 ? (
            parkingSpotList.map((parkingSpot, j) => (

                <Row key={parkingSpot.id}> {(parkingSpot.carPark != null && parkingSpot.carPark.id === carPark.id ) ?
                  (parkingSpot.available ? <div className = "green-hspot"/> : <div className = "red-hspot"/>) : ('') }
                </Row>

         ))):(
            !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
            </div>
        ))}</div>
            ))): (!load && (
            <div className="alert alert-warning">
              <Translate contentKey="smartParkingApp.carPark.home.notFound">No Carpark found</Translate>
            </div>))}
          </div>

          </div>
            ) : (<div className="parkingLot">
<div>
  {carParkList && carParkList.length > 0 ? (
  carParkList.map((carPark, i) => (
  <div key={carPark.id}>
  <Row> {carPark.name}</Row>
  {parkingSpotList && parkingSpotList.length > 0 ? (
  parkingSpotList.map((parkingSpot, j) => (

  <Row key={parkingSpot.id}> {(parkingSpot.carPark != null && parkingSpot.carPark.id === carPark.id ) ?
  (parkingSpot.available ? <div className = "green-hspot"/> : <div className = "red-hspot"/>) : ('') }
</Row>

))):(
!loading && (
<div className="alert alert-warning">
  <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
</div>
))}</div>
  ))): (!load && (
<div className="alert alert-warning">
<Translate contentKey="smartParkingApp.carPark.home.notFound">No Carpark found</Translate>
</div>))}
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
  loading: storeState.parkingSpot.loading,
  load: storeState.carPark.loading,
  carParkList: storeState.carPark.entities,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
  updateEntity,
  getCarpark,
  getSearchCarpark,
};
type DispatchProps = typeof mapDispatchToProps;
type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps, mapDispatchToProps)(Home);
