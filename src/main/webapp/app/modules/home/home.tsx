import './home.scss';

import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { connect } from 'react-redux';
import { Container, Row, Col, Alert } from 'reactstrap';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from 'app/entities/parking-spot/parking-spot.reducer';

export interface IHomeProp extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}
//export type IHomeProp = StateProps;

export const Home = (props: IHomeProp) => {
  const { parkingSpotList, loading} = props;

  const [search, setSearch] = useState('');

  useEffect(() => {
    props.getEntities();
  }, []);

  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    props.getEntities();
  };

  return (
    <Row>
      <Col md="9">
        <h2>
          <Translate contentKey="home.title">Welcome SmartParking!</Translate>
        </h2>

        <div>
          <Container>
              <Row>
              {parkingSpotList && parkingSpotList.length > 0 ? (
              parkingSpotList.map((parkingSpot, i) => (
                parkingSpot.available ? <div className = "green-hspot"/> : <div className = "red-hspot"/>
              ))) : (
                !loading && (
                  <div className="alert alert-warning">
                    <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
                  </div>
                ))}
              </Row>
            <Row className ="blank"/>
          </Container>
        </div>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ parkingSpot }: IRootState, storeState) => ({
  parkingSpotList: parkingSpot.entities,
  loading: parkingSpot.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};
type DispatchProps = typeof mapDispatchToProps;
type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps, mapDispatchToProps)(Home);
