import React from "react";
import { Link } from "react-router-dom";
import Card from "./Card";
import styles from "./Navigation.module.css";
import baseStyles from "../../css/base.module.css";

function Navigation() {
  return (
    <section className={styles.navigation}>
      <Link className={baseStyles.link} to="/overview">
        <Card>Visão Geral</Card>
      </Link>
      <Link className={baseStyles.link} to="/performance">
        <Card>Desempenho</Card>
      </Link>
      <Link className={baseStyles.link} to="/register-operation">
        <Card>Registrar Operação</Card>
      </Link>
      <Link className={baseStyles.link} to="/tax">
        <Card>Imposto</Card>
      </Link>
    </section>
  );
}

export default Navigation;
