import jwt_decode from "jwt-decode";
import { useEffect } from "react";
import { ErrorBoundary } from "react-error-boundary";
import { Route, Routes } from "react-router-dom";
import FinishedMatchesList from "./admin/users/matches/FinishedMatchesList";
import StartedMatchesList from "./admin/users/matches/StartedMatchesList";
import AppNavbar from "./AppNavbar";
import Login from "./auth/login";
import Logout from "./auth/logout";
import Register from "./auth/register";
import Home from "./home";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import SwaggerDocs from "./public/swagger";
import JoinLobby from "./lobby/JoinLobby";
import MatchList from "./player/matches/MatchList";
import PrivateRoute from "./privateRoute";
import PlanList from "./public/plan";
import PlayerMatches from "./player/matches";
import Tablero from "./components/board/index";
import Lobby from "./lobby/Lobby"
import ProfileList from "./player/profile/ProfileList"
import DeveloperList from "./developers";
import EditProfile from "./player/profile/EditProfile";
import Criterios from "./components/criterios/criterios";
import UserCreateAdmin from "./admin/users/UserCreateAdmin";
import Statistics from "./player/statistics/index";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {

  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let playerRoutes = <></>;
  let userRoutes = <></>;
  let vetRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/new" exact={true} element={<PrivateRoute><UserCreateAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route
            path="/matches/finished"
            exact={true}
            element={
              <PrivateRoute>
                <FinishedMatchesList />
              </PrivateRoute>
            }
          />
          <Route
            path="/matches/started"
            exact={true}
            element={
              <PrivateRoute>
                <StartedMatchesList />
              </PrivateRoute>
            }
          />
          <Route path="/developers" element={<DeveloperList />} />
        </>)
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/playerMatches" exact={true} element={<PrivateRoute><PlayerMatches /></PrivateRoute>} />
          <Route path="/users/:id" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/statistics" exact={true} element={<PrivateRoute><Statistics /></PrivateRoute>} />
        </>)
    }
  })
  if (!jwt) {
    publicRoutes = (
      <>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          <Route path="/plans" element={<PlanList />} />
          <Route path="/docs" element={<SwaggerDocs />} />
          <Route path="/matches/started" element={<StartedMatchesList />} />
          <Route path="/matches/finished" element={<FinishedMatchesList />} />
          <Route path="/matches/:matchId/:playerId" element={<Tablero />} />
          <Route path="/matches/:matchId" element={<Criterios />} />
          <Route path="/lobbies/:lobbyId" element={<Lobby />} />
          <Route path="/joinLobby" element={<JoinLobby />} />
          <Route path="/profileList" element={<ProfileList />} />
          <Route path="/editProfile" element={<EditProfile />} />
          <Route path="/matchList" element={<MatchList />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
          {vetRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
