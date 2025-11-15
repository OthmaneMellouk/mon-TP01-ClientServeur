--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-11-15 12:55:51

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4902 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 23204)
-- Name: Cap; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Cap" (
    "idMesure" integer NOT NULL,
    client_id integer NOT NULL,
    temperature double precision,
    pression double precision,
    humidite double precision,
    date_mesure timestamp with time zone
);


ALTER TABLE public."Cap" OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 23199)
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    client_id integer NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(20) NOT NULL
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- TOC entry 4896 (class 0 OID 23204)
-- Dependencies: 218
-- Data for Name: Cap; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Cap" ("idMesure", client_id, temperature, pression, humidite, date_mesure) FROM stdin;
5379	2	11	11	11	2025-11-13 10:45:24.742826+01
8706	2	22	22	22	2025-11-13 10:46:14.20695+01
3537	1	44	44	44	2025-11-13 11:31:52.447801+01
1351	1	55	55	55	2025-11-13 11:32:05.253744+01
5312	1	77	77	77	2025-11-13 11:32:15.400349+01
\.


--
-- TOC entry 4895 (class 0 OID 23199)
-- Dependencies: 217
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user (client_id, password, role) FROM stdin;
1	admin	admin
2	client	client
\.


--
-- TOC entry 4748 (class 2606 OID 23208)
-- Name: Cap Cap_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Cap"
    ADD CONSTRAINT "Cap_pkey" PRIMARY KEY ("idMesure");


--
-- TOC entry 4746 (class 2606 OID 23203)
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (client_id);


--
-- TOC entry 4749 (class 2606 OID 23209)
-- Name: Cap client_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Cap"
    ADD CONSTRAINT client_id FOREIGN KEY (client_id) REFERENCES public.app_user(client_id);


-- Completed on 2025-11-15 12:55:51

--
-- PostgreSQL database dump complete
--

