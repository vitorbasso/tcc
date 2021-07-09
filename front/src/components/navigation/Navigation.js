import React from "react";
import { Link } from "react-router-dom";
import Card from "./Card";
import styles from "./Navigation.module.css";

function Navigation() {
  return (
    <section className={styles.navigation}>
      <Link to="/overview">
        <Card>Visão Geral</Card>
      </Link>
      <Link to="/performance">
        <Card>Desempenho</Card>
      </Link>
      <Link to="/register-operation">
        <Card>Registrar Operação</Card>
      </Link>
      <Link to="/tax">
        <Card>Imposto</Card>
      </Link>
    </section>
  );
}

export default Navigation;
