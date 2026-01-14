# Repository: martian-bank

## Overview
Martian Bank is a microservices-based cloud-native banking application developed by Cisco Open as a demonstration platform for financial services in containerized environments. The system simulates core banking operations including user authentication, account lifecycle management, peer-to-peer and email-based fund transfers (Zelle-style), loan applications, and ATM location discovery with interplanetary filtering. It is designed for development, testing, and demonstration purposes, supporting deployment via Docker Compose for local execution or Helm on Kubernetes for production-like orchestration. The architecture integrates a React frontend (`ui`), multiple backend microservices (Node.js and Python Flask), MongoDB for persistence, NGINX as a reverse proxy, and observability/performance tooling (APIClarity, Locust). Communication occurs over HTTP and gRPC, with Protocol Buffers defining canonical service contracts.

**File Structure overview**
- `customer-auth`: Node.js service handling user registration, login, JWT-based session management, and profile operations.
- `loan`: Python Flask service processing loan applications with eligibility validation and decision logging via dual REST/gRPC APIs.
- `atm-locator`: Node.js service providing ATM discovery with real-time availability and interplanetary capability filtering.
- `nginx`: NGINX reverse proxy and API gateway routing incoming requests to appropriate backend services based on path.
- `ui`: React-based frontend application using Vite, Redux Toolkit, and RTK Query for responsive banking UI with route protection and form workflows.
- `images`: Static asset folder containing visual resources (logos, icons) used by the frontend.
- `scripts`: Bash and AppleScript automation scripts for orchestrating local development environment startup and teardown on macOS.
- `LICENSE`: BSD 3-Clause License governing software usage and redistribution.
- `integrations`: Infrastructure scripts deploying API Clarity with Istio sidecar injection for API observability in Kubernetes.
- `performance_locust`: Locust-based performance testing suite simulating end-to-user workflows across authentication, transfers, and loans.
- `martianbank`: Helm chart for Kubernetes deployment, templating all services with configurable features (NGINX, Locust, MongoDB).
- `transactions`: Python Flask service managing internal and external (Zelle) financial transfers with transaction history retrieval.
- `docker-compose.yaml`: Docker Compose configuration defining 12 services with network isolation, port mappings, and startup dependencies.
- `CODE_OF_CONDUCT.md`: Contributor Covenant Code of Conduct ensuring respectful community interactions.
- `dashboard`: Flask-based backend service acting as an API aggregation layer and server-side HTML renderer for form workflows.
- `licenses`: Automation scripts generating a unified Software Bill of Materials (SBOM) from Python and JavaScript dependencies.
- `CONTRIBUTING.md`: Guidelines for contributing, including issue reporting, pull requests, and security disclosures.
- `protobufs`: Protocol Buffer (proto3) schema definitions for gRPC services across accounts, loans, and transactions.
- `SECURITY.md`: Security policy outlining vulnerability reporting procedures and response timelines.
- `README.md`: Project documentation detailing architecture, deployment, and roadmap.
- `accounts`: Python Flask service managing bank account creation, retrieval, and lifecycle operations via dual REST/gRPC APIs.

## Business Domain Summary
Martian Bank implements core digital banking workflows: users register and authenticate to access financial services, create accounts (one per type) with a $100 starting balance, perform internal and Zelle-style transfers (restricted to checking accounts), apply for loans with predefined terms, and locate ATMs using real-time status filters. Business rules enforce identity uniqueness (email), password complexity (8+ chars, special characters), and data privacy via client-side masking of sensitive fields. All financial operations are auditable via timestamps and identifiers, and user context is correlated across services using email. The domain emphasizes secure, compliant, and observable interactions across a distributed system.

## Analysis dimensions:
**Purpose / Functionality:**
- User authentication lifecycle: registration, login, profile retrieval/update, and logout via `customer-auth` with JWT issuance.
- Loan application processing: submission, eligibility validation (linked account, positive amount), approval/decline decision logging via `loan`.
- ATM location discovery: list all ATMs or filter by `isOpenNow` and `isInterPlanetary` via `atm-locator`.
- Reverse proxy and API gateway: `nginx` routes `/api/users`, `/api/atm`, `/api/account`, `/api/transaction`, `/api/loan` to respective backend services.
- Static asset hosting: `images` serves logos and icons to the frontend UI.
- Local development orchestration: `scripts/run_local.sh` and `stop_local.sh` launch/terminate services using AppleScript on macOS.
- Software license compliance: `licenses/report.sh` generates SBOM from `pip` and `npm` dependencies.
- Kubernetes deployment orchestration: `martianbank` Helm chart deploys all components with configurable feature flags.
- Financial transaction processing: account-to-account and email-based (Zelle) transfers via `transactions`, with history retrieval.
- Multi-container orchestration: `docker-compose.yaml` defines 12 services with networking, ports, and `depends_on` dependencies.
- Community governance: `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, and `SECURITY.md` establish behavioral, contribution, and security policies.
- API observability: `integrations/apiclarity.sh` deploys API Clarity with Istio sidecar injection and Envoy Wasm traffic capture.
- Performance testing: `performance_locust` simulates user workflows (auth, transfers, loans) using synthetic data and sequential execution.
- Dashboard and API aggregation: `dashboard` proxies `/api/users/*` and `/api/atm/*`, and exposes `/account/*`, `/transaction/*`, `/loan/*` with HTML forms.
- Account lifecycle management: creation, retrieval by email or ID, duplicate prevention via `accounts`.
- gRPC contract definition: `protobufs` defines `AccountDetailsService`, `LoanService`, `TransactionService` with typed messages.
- Frontend banking interface: `ui` provides SPA for login, account management, transfers, loans, ATM search, and transaction history.
- Client-side state management: Redux Toolkit manages authentication, account, loan, transfer, and transaction state in `ui`.
- Environment-aware API routing: `ui` resolves backend endpoints using `VITE_*_URL` environment variables with localhost fallbacks.

**Abstractions & Critical Structures:**
- Microservices architecture with separation of concerns: identity (`customer-auth`), financial operations (`accounts`, `transactions`, `loan`), geospatial (`atm-locator`), aggregation (`dashboard`), infrastructure (`nginx`, `martianbank`).
- Dual-protocol API pattern: gRPC and REST endpoints in `loan`, `transactions`, `accounts`, `dashboard`, selected via `SERVICE_PROTOCOL` environment variable.
- Protocol Buffers (proto3) used in `protobufs` to define canonical data contracts and gRPC service interfaces.
- Helm chart templating in `martianbank` using Go templates for naming (`_helpers.tpl`), conditional resources (`.Values.nginx.enabled`), and version-aware Ingress.
- Docker-based containerization across all services using multi-stage or conventional builds with environment-driven configuration.
- Modular Express.js architecture in `customer-auth` and `atm-locator` with routes, controllers, middleware, models, and config separation.
- Centralized error handling via Express middleware in Node.js services and structured responses in Python services.
- Worker thread offloading in `customer-auth` using Node.js `worker_threads` to prevent event loop blocking during auth.
- Sequential task execution in `performance_locust` using `SequentialTaskSet` to model real user navigation.
- Environment-based conditional logic in error responses (stack trace suppression) and service routing.
- Use of `DotMap` in Python services (`loan`, `transactions`, `dashboard`) for dot-accessible config and request/response objects.
- Kubernetes-native deployment: Helm charts, Ingress with TLS, PersistentVolumeClaims, HorizontalPodAutoscaler, test hooks.
- Docker Compose-defined bridge network (`bankapp-network`) enabling internal DNS-based service discovery.
- OpenAPI 3.0.0 specification in `swagger.yaml` for API documentation in `customer-auth` and `atm-locator`.
- Functional React components with hooks (`useState`, `useEffect`, `useSelector`, `useDispatch`, `useNavigate`) in `ui`.
- Reusable UI components via `react-bootstrap` and `MDB React UI Kit` in `ui`.
- Composition pattern using `<Outlet />` from React Router v6 for protected routes (`PrivateRoute.jsx`).
- Higher-Order Component (HOC)-like pattern in `PrivateRoute.jsx` for route protection.
- Modular Redux state architecture using domain-specific slices (`authSlice`, `accountSlice`, `loanSlice`, etc.).
- RTK Query integration via `apiSlice` for declarative data fetching, caching, and auto-generated hooks.
- Tag-based caching in `usersApiSlice.js` using `tagTypes: ['User']` and `invalidatesTags: ['User']`.
- Client-side session persistence via `localStorage.getItem('userInfo')` synchronized in `authSlice.js`.

**API definitions:**
- `customer-auth`:
  - `POST /api/users` – Register user (JSON: `{name, email, password}`), returns 201.
  - `POST /api/users/auth` – Authenticate user (JSON: `{email, password}`), returns JWT in cookie.
  - `POST /api/users/logout` – Logout (no token invalidation).
  - `POST /api/users/profile` – Retrieve profile by email.
  - `PUT /api/users/profile` – Update profile (insecure, lacks auth).
- `loan`:
  - `POST /loan/request` – Submit loan (JSON: `{email, account_number, loan_amount, ...}`), returns `{approved: boolean}`.
  - `POST /loan/history` – Retrieve loan history by email.
  - gRPC: `ProcessLoanRequest(LoanRequest) → LoanResponse`, `getLoanHistory(LoansHistoryRequest) → LoansHistoryResponse`.
- `atm-locator`:
  - `POST /api/atm/` – List ATMs with optional `isOpenNow`, `isInterPlanetary` filters.
  - `GET /api/atm/{id}` – Retrieve ATM by ID.
  - `POST /atm/add` – Create ATM (authenticated).
- `transactions`:
  - `POST /transfer` – Internal transfer (JSON: `{sender_account_number, receiver_account_number, amount}`).
  - `POST /zelle` – Email-based transfer.
  - `POST /transaction-history` – Retrieve by `account_number`.
  - gRPC: `sendMoney`, `Zelle`, `getTransactionsHistory`, `getTransactionByID`.
- `accounts`:
  - `POST /create-account` – Create account (JSON: `{email_id, account_type, ...}`).
  - `POST /get-all-accounts` – List by `email_id`.
  - `POST /account-detail` – Retrieve by `account_number`.
  - gRPC: `createAccount`, `getAccounts`, `getAccountDetails`.
- `dashboard`:
  - Proxies `/api/users/*` to `customer-auth`.
  - Proxies `/api/atm/*` to `atm-locator`.
  - Exposes `/account/*`, `/transaction/*`, `/loan/*` with form-based workflows and HTML rendering.
- `nginx`:
  - `/` → `ui:3000`
  - `/api/users` → `customer-auth:8000/api/users/`
  - `/api/atm` → `atm-locator:8001/api/atm/`
  - `/api/account`, `/api/transaction`, `/api/loan` → `dashboard:5000/`
- `protobufs`:
  - gRPC service contracts for `AccountDetailsService`, `LoanService`, `TransactionService` with typed request/response messages.
- `ui`:
  - Auto-generated RTK Query hooks: `useLoginMutation`, `useLogoutMutation`, `useRegisterMutation`, `useUpdateUserMutation`, `useGetAtmsMutation`, `useGetAtmByIdQuery`, `usePostTransferMutation`, `usePostTransferExternalMutation`, `useCreateAccountMutation`, `useGetAllAccountsMutation`, `useGetTransactionsMutation`, `usePostLoanMutation`, `useGetApprovedLoansMutation`.
  - Backend API endpoints inferred from code: `${usersUrl}/auth`, `${atmUrl}`, `${transferUrl}`, `${accsUrl}create`, `${loanUrl}`, etc.

**Data Handling (DB interactions, validation):**
- MongoDB used as primary database across all services, accessed via Mongoose (Node.js) or PyMongo (Python).
- Schema-level validation in `customer-auth` and `atm-locator` models (required fields, unique constraints).
- No ORM or schema validation in Python services (`loan`, `transactions`, `accounts`, `dashboard`); relies on application-level checks.
- Input validation limited to presence and type conversion (e.g., `float(request.form['amount'])`), with no sanitization or format checks.
- Data lifecycle operations inferred: all loan requests are persisted regardless of outcome; ATM data is reseeded on startup (non-production).
- Audit and temporal tracking via `createdAt`, `updatedAt`, `time_stamp`, `timestamp` fields in multiple services.
- Identity correlation via `email` and `email_id` across services, suggesting domain-wide user context.
- Hardcoded MongoDB credentials (`root:example`) in `docker-compose.yaml`, `martianbank` Helm chart, and `nginx` configuration.
- No use of Kubernetes Secrets for sensitive data (e.g., `JWT_SECRET`, `DB_URL`) — stored in ConfigMap or environment variables.
- Sensitive PII (SSN, government ID, account number) stored in plaintext without encryption in `accounts`, `loan`, and `dashboard`.
- Passwords hashed with bcrypt (salt rounds = 10) before persistence in `customer-auth`.
- Client-side password validation enforces 8+ characters, uppercase, lowercase, digit, special character via regex in `RegisterScreen.jsx`.
- Government ID types restricted to Passport, Driver's License, SSN.
- Loan types and terms hardcoded (e.g., BaseCamp: 5.99%, 10y).
- Sensitive data masked in UI (account numbers via `.slice(-4)`, disabled inputs).
- Balance formatting using `.toFixed(2)`.

**Error & Resilience (error handling, retries):**
- Centralized error handling in `customer-auth` and `atm-locator` via Express middleware, returning standardized JSON responses.
- Try/catch block in `Header.jsx` logout handler logs errors to console and shows toast notification.
- Success and error feedback via `react-toastify` with dark theme and auto-dismiss after 500ms.
- Loading states (`isLoading`) prevent duplicate submissions in `ui`.
- No retry mechanisms, circuit breakers, or fallback strategies in any service.
- No liveness or readiness probes defined in Kubernetes manifests (`martianbank`).
- `docker-compose.yaml` uses `restart: always` for fault tolerance but no health checks.
- `integrations/apiclarity.sh` lacks error handling and uses hardcoded delays (`sleep 20`, `sleep 40`) instead of readiness checks.
- `scripts/run_local.sh` has basic tool validation but no error recovery.
- `performance_locust` has no error handling in test scripts or orchestration.
- `transactions` and `accounts` lack try-except blocks around database operations.
- Helm test hook in `martianbank` uses `restartPolicy: Never`, so failed tests do not retry.
- State slices in `ui` include `error` and `isLoading` fields but lack reducers to update them, suggesting external management.
- No circuit breakers or retry policies evident.
- Reliance on default RTK Query behavior for managing loading, success, and error states.

**Configuration (env vars, feature flags, settings):**
- Environment variables used extensively: `DB_URL`, `JWT_SECRET`, `SERVICE_PROTOCOL`, `PORT`, `NODE_ENV`, `ACCOUNT_HOST`, `TRANSACTION_HOST`, `LOAN_HOST`, `CUSTOMER_AUTH_HOST`, `ATM_LOCATOR_HOST`.
- Feature flags in `martianbank` Helm chart: `.Values.nginx.enabled`, `.Values.locust.enabled`, `.Values.mongodb.enabled`, `.Values.autoscaling.enabled`.
- `SERVICE_PROTOCOL` environment variable controls gRPC vs HTTP mode in `loan`, `transactions`, `accounts`, `dashboard`.
- `.env` files used in `customer-auth`, `atm-locator`, `ui`, and `dashboard` for local configuration.
- Configuration centralized in `martianbank/values.yaml` and injected via ConfigMap.
- `docker-compose.yaml` uses inline environment variables and `.env` file references.
- `integrations/apiclarity.sh` dynamically configures `nginx.dashboardIP` after deployment.
- `performance_locust/api_urls.py` uses environment variables with localhost fallbacks.
- `ui` uses `import.meta.env.VITE_*_URL` to resolve backend endpoints with fallback to constants in `ApiUrls`.
- Toast notifications configured with: `autoClose: 500`, `theme: 'dark'`, `draggable`, `pauseOnHover`, `closeOnClick`, `hideProgressBar`.
- Map center coordinates hardcoded in `AtmScreen`: `[37.77175, -81.1901]`.
- Leaflet tile layer URL hardcoded: `https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png`.
- Redirection path `/login` is hardcoded in `PrivateRoute.jsx`.

**Workflow / State Management (status tracking, orchestration):**
- Linear authentication workflow: register → login → access protected endpoints → logout (in `customer-auth` and `performance_locust`).
- Loan lifecycle: request → validate → approve/decline → persist with `status` and `timestamp`.
- Transaction lifecycle: initiate → validate → update balances → log with `time_stamp`.
- Account creation: validate → generate number → set balance → insert with `created_at`.
- ATM status tracked via `isOpen` and `timings` fields, with real-time determination based on system time.
- `dashboard` orchestrates form-based workflows: `create_account_form.html` → `detail_result.html`, `loan_form.html` → `loan_result.html`.
- `scripts/run_local.sh` orchestrates sequential startup: JavaScript services first, then Python, with `sleep 2` delays.
- `integrations/apiclarity.sh` orchestrates: cleanup → namespace creation → Istio injection → Martian Bank install → API Clarity install → UI exposure → reconfiguration → load generation.
- `performance_locust/locust.sh` runs tests sequentially with sleep intervals.
- `docker-compose.yaml` uses `depends_on` for startup order but no readiness checks.
- Authentication state managed via Redux (`useSelector((state) => state.auth.userInfo)`) — serves as gatekeeper for UI rendering and route access.
- Logout workflow: Call `logoutApiCall({ email })`, unwrap promise, dispatch `logout()`, show toast, navigate to `/login`.
- `PrivateRoute.jsx` implements binary access flow: authenticated → render `<Outlet />`, unauthenticated → redirect to `/login`.
- Global state management delegated to `store.js` with domain-specific reducers.
- Client-side navigation workflow managed entirely by React Router.
- Lifecycle: On mount → fetch data → update state → render UI.
- Conditional rendering based on `userInfo`, `isLoading`, `selectedAccount`, etc.
- Navigation orchestrated via `useNavigate()` on success/error.
- Form workflows: fill → validate → submit → handle response → redirect.
- Auto-fill logic: account selection → populate ID; loan type → populate rate/term.
- Terms acceptance enforced via required checkbox and modal.
- Authentication workflow: Unauthenticated → authenticated (via `setCredentials`) and authenticated → unauthenticated (via `logout`) — `authSlice.js`.
- Account lifecycle: Create, retrieve, select, delete — `accountSlice.js`.
- Loan workflow: Apply → view approved history — `loanApiSlice.js` and `loanSlice.js`.
- Transfer workflow: Initiate → track current transfer state — `transferApiSlice.js` and `transferSlice.js`.
- Transaction workflow: Fetch history → store in state — `transactionApiSlice.js` and `transactionSlice.js`.

**Technologies / Frameworks:**
- Node.js (v14, EOL) – used in `customer-auth`, `atm-locator`, `nginx`, `ui` [Evidence: `Dockerfile`, `package.json`].
- Python 3 – used in `loan`, `transactions`, `accounts`, `dashboard`, `scripts`, `licenses`, `integrations` [Evidence: syntax, `python3 -m venv`, `python:3.9-slim`].
- Express.js (^4.18.2) – web framework in `customer-auth`, `atm-locator` [Evidence: `package.json`].
- Flask (^1.1.4) – web framework in `loan`, `transactions`, `accounts`, `dashboard` [Evidence: `requirements.txt`].
- MongoDB – NoSQL database accessed via Mongoose (^7.x) and PyMongo [Evidence: `DB_URL`, `pymongo.MongoClient`].
- Docker – containerization platform [Evidence: `Dockerfile` in all services].
- Kubernetes – orchestration platform [Evidence: `apiVersion: apps/v1`, `v1`, `autoscaling/v2` in `martianbank`].
- Helm (v2+) – deployment and packaging tool [Evidence: `apiVersion: v2`, `Chart.yaml`].
- gRPC – inter-service communication [Evidence: `grpcio`, `grpcio-tools`, `.proto` files].
- Protocol Buffers (proto3) – message serialization [Evidence: `syntax = "proto3";` in `protobufs/*.proto`].
- NGINX – reverse proxy and API gateway [Evidence: `default.conf`, `proxy_pass`].
- React (18.2.0) – frontend UI framework [Evidence: `licenses.txt`, `package.json`].
- Redux Toolkit (v1.9.7) – state management [Evidence: `package-lock.json`].
- Vite (v4.3.2) – build tool and dev server [Evidence: `package.json`].
- Axios (v1.5.1) – HTTP client [Evidence: `package-lock.json`].
- Locust – load testing tool [Evidence: `locust`, `Dockerfile` in `performance_locust`].
- APIClarity – API observability platform [Evidence: `integrations/apiclarity.sh`, `NOTE.md`].
- Swagger (OpenAPI 3.0.0) – API documentation [Evidence: `swagger.yaml`, `swagger-jsdoc`, `swagger-ui-express`].
- Bash – scripting in `scripts`, `integrations`, `performance_locust`, `licenses` [Evidence: `.sh` files].
- AppleScript – GUI automation in `scripts` for macOS Terminal control.
- Leaflet (v1.9.4) – interactive maps [Evidence: `package-lock.json`, `AtmScreen.jsx`].
- Google Maps (via @googlemaps/js-api-loader v1.16.2) – map integration [Evidence: `package-lock.json`].
- react-bootstrap (v2.7.4) – Bootstrap components for React [Evidence: `package.json`].
- react-router-dom (v6.11.0) – client-side routing [Evidence: `package.json`].
- react-toastify (v9.1.2) – notification system [Evidence: `package.json`].
- MDB React UI Kit – UI components [Evidence: `TransactionScreen.jsx`].

**Security rules:**
- JWT-based authentication in `customer-auth` using `jsonwebtoken` and `JWT_SECRET` from environment.
- Passwords hashed with bcrypt (salt rounds = 10) before persistence in `customer-auth`.
- CORS configured with `credentials: true, origin: true` in `customer-auth` and `atm-locator` – potentially insecure.
- Intended but unimplemented cookie security flags (`httpOnly`, `secure`, `sameSite`) in `customer-auth`.
- `PUT /profile` in `customer-auth` lacks authentication middleware – potential IDOR vulnerability.
- Logout does not invalidate tokens – session fixation risk.
- No authentication or authorization in `loan`, `transactions`, `accounts`, `dashboard` – assumes proxy enforcement.
- Hardcoded MongoDB credentials (`root:example`) in `docker-compose.yaml`, `martianbank`, and `nginx`.
- `JWT_SECRET` set to empty string in `martianbank/configmap.yaml` – insecure default.
- No TLS/SSL in `nginx`, `loan`, `transactions`, `accounts`, `dashboard` – all HTTP.
- Sensitive PII (SSN, government ID, email) stored in plaintext in MongoDB.
- Debug mode enabled in Flask services (`debug=True`) – potential information disclosure.
- No rate limiting, brute force protection, or account lockout mechanisms.
- Node.js 14 in Dockerfiles is end-of-life – security risk.
- No use of Kubernetes Secrets – sensitive data in ConfigMap.
- `SECURITY.md` mandates encrypted email reporting and coordinated disclosure.
- `CODE_OF_CONDUCT.md` prohibits doxxing and harassment.
- `CONTRIBUTING.md` prohibits public disclosure of security bugs.
- Client-side authorization check based on presence of `userInfo` in Redux store (`PrivateRoute.jsx`, `Header.jsx`).
- Logout triggers both server-side session invalidation (via API call) and client-side state clearance (Redux dispatch).
- No input sanitization or XSS protection mechanisms implemented.
- Potential XSS risk if `children` prop in `FormContainer.jsx` includes untrusted content.
- Hardcoded asset paths may expose internal structure if misconfigured.
- Toast messages do not expose sensitive error details.
- Commented-out JWT cookie code (`Cookies.remove('jwt')`) suggests migration from secure HTTP-only cookie to less secure `localStorage`.
- User credentials and session data stored in plain text in `localStorage`, posing XSS risks — `authSlice.js`.
- Backend service URLs exposed in client-side code (`apiUrls.js`), increasing risk of reconnaissance.
- Use of `http://localhost` and `http://127.0.0.1` in `apiUrls.js` indicates absence of TLS in current configuration.
- No role-based or permission-level access control implemented.
- No token expiration or session validity checks performed in components.
- Authentication state assumed valid if `userInfo` exists — no server-side validation at route level.
- Password fields use `type='password'` and enforce complexity via regex.
- Email validated using regex pattern in form controls.
- Sensitive fields rendered as disabled inputs.
- Account numbers masked using `.slice(-4)`.
- Terms and conditions acceptance required via checkbox.
- Error messages sanitized using optional chaining to avoid stack trace exposure.
- Potential XSS risk if backend returns untrusted HTML in fields like `transaction.reason`.
- No observed client-side input sanitization or encoding for dynamic content.
- No evidence of CSRF token handling in `FormData` submissions.
- No explicit encryption, secure storage, or XSS mitigation logic visible.