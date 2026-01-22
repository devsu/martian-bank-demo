# Service Layer Tasks

## Overview

Implement business logic services. Services initially work with mocked data, then get wired to repositories.

## Tasks

### [TASK-010] - [AI] Implement RandomizationUtils

Create utility class with static method `selectRandom(List<T> items, int maxCount)` using Collections.shuffle(). Handle empty list and null input gracefully.

---

### [TASK-011] - [AI] Create AtmService interface and implementation

Create AtmService interface with `findAtms(AtmSearchRequest request)` method. Implement AtmServiceImpl with mocked data, filtering logic for isOpenNow/isInterPlanetary, and RandomizationUtils for max 4 results.

---

### [TASK-012] - [AI] Wire AtmController to AtmService

Inject AtmService into AtmController via constructor injection. Replace hardcoded mocked data with service delegation.
