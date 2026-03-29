import React, { useState, useEffect, useRef } from 'react';
import {
    Navbar,
    NavbarBrand,
    NavLink,
    NavItem,
    Nav,
    NavbarText,
    NavbarToggler,
    Collapse
} from 'reactstrap';
import { Link, useLocation } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import reglas from './static/images/reglas/reglasMedium.jpg';

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const [showImage, setShowImage] = useState(false);
    const imageRef = useRef(null);
    const location = useLocation();

    const toggleNavbar = () => setCollapsed(!collapsed);


    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt]);

    const handleToggleImage = () => {
        setShowImage((prevState) => !prevState);
    };

    const handleClickOutside = (event) => {
        if (imageRef.current && !imageRef.current.contains(event.target)) {
            setShowImage(false);
        }
    };

    useEffect(() => {
        if (showImage) {
            document.addEventListener("mousedown", handleClickOutside);
        } else {
            document.removeEventListener("mousedown", handleClickOutside);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [showImage]);

    let adminLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;
    let playerLinks = <></>;
    let ownerLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users">Usuarios</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">API Docs</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/developers">Developers</NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "PLAYER") {
            playerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/playerMatches">Mis Partidas</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink
                            style={{ color: "white", cursor: "pointer" }}
                            id="rules"
                            onClick={handleToggleImage}
                        >
                            Reglas
                        </NavLink>
                    </NavItem>
                    {showImage && (
                        <div
                            ref={imageRef}
                            style={{
                                position: "absolute",
                                top: "10px",
                                left: "50%",
                                transform: "translateX(-50%)",
                                zIndex: 10,
                                background: "white",
                                padding: "7px",
                                border: "1px solid #ccc",
                                borderRadius: "8px",
                                boxShadow: "0 4px 6px rgba(0,0,0,0.1)"
                            }}
                        >
                            <img
                                src={reglas}
                                alt="Reglas"
                                style={{ maxWidth: "850px", maxHeight: "850px" }}
                            />
                        </div>
                    )}
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/statistics">Estadísticas</NavLink>
                    </NavItem>
                </>
            )
        }
    });

    if (!jwt) {
        playerLinks = (
            <>
            <NavItem>
                        <NavLink
                            style={{ color: "white", cursor: "pointer" }}
                            id="rules"
                            onClick={handleToggleImage}
                        >
                            Reglas
                        </NavLink>
                    </NavItem>
                    {showImage && (
                        <div
                            ref={imageRef}
                            style={{
                                position: "absolute",
                                top: "10px",
                                left: "50%",
                                transform: "translateX(-50%)",
                                zIndex: 10,
                                background: "white",
                                padding: "7px",
                                border: "1px solid #ccc",
                                borderRadius: "8px",
                                boxShadow: "0 4px 6px rgba(0,0,0,0.1)"
                            }}
                        >
                            <img
                                src={reglas}
                                alt="Reglas"
                                style={{ maxWidth: "850px", maxHeight: "850px" }}
                            />
                        </div>
                    )}
            </>
        )

        publicLinks = (
            <>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="register" tag={Link} to="/register">Registrarse</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="login" tag={Link} to="/login">Iniciar sesión</NavLink>
                </NavItem>
            </>
        )
    } else {
        userLogout = (
            <>
                <NavbarText style={{ color: "white" }} tag={Link} to="/profileList">{username}</NavbarText>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="logout" tag={Link} to="/logout">Cerrar sesión</NavLink>
                </NavItem>
            </>
        )
    }

    return (
        <div>
            <Navbar expand="md" dark color="dark">
                <NavbarBrand href="/">
                    <img alt="logo" src="/logo1-recortado.png" style={{ height: 40, width: 40 }} />
                    <span style={{ marginLeft: '10px' }}>Los Mapas Del Reino</span>
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {adminLinks}
                        {ownerLinks}
                        {playerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {userLogout}
                    </Nav>
                </Collapse>
            </Navbar>
        </div>
    );
}

export default AppNavbar;
