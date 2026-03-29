import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

const USERS_PER_PAGE = 5;

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [pageNumber, setPageNumber] = useState(1)
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const getPage = (usersList) => {
    const start = (pageNumber - 1) * USERS_PER_PAGE;
    const end = start + USERS_PER_PAGE;
    return usersList.slice(start, end);
  }

  const maxPages = Math.ceil(users.length / USERS_PER_PAGE);  

  const userList = getPage(users).map((user) => {
    return (
      <tr key={user.id}>
        <td>{user.username}</td>
        <td>{user.authority.authority}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + user.id}
              tag={Link}
              to={"/users/" + user.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + user.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/users/${user.id}`,
                  user.id,
                  [users, setUsers],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });
  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Users</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/users/new">
        Add User
      </Button>
      <div>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Username</th>
              <th>Authority</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </Table>

        <div style={{display: 'flex', justifyContent: 'space-around', verticalAlign: 'center'}}>
          <button onClick={() => pageNumber - 1 > 0 ? setPageNumber(pageNumber - 1) : pageNumber}>Prev</button>
            { pageNumber !== 1 && <a onClick={() => setPageNumber(1)} style={{cursor: 'pointer'}}>1</a> }
              { pageNumber !== 1 && <>...</> }
                <p style={{color: 'blue', marginBottom: 0}}> { pageNumber } </p>
              { pageNumber !== maxPages && <>...</> }
            { pageNumber !== maxPages && <a onClick={() => setPageNumber(maxPages)} style={{cursor: 'pointer'}}>{maxPages}</a> }
          <button onClick={() => pageNumber + 1 <= maxPages ? setPageNumber(pageNumber + 1) : pageNumber}>Next</button>
        </div>

      </div>
    </div>
  );
}
