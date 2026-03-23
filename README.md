# CondoMonitor

A configurable web monitoring and alerting system that periodically scrapes real estate listing sites, detects new listings matching user-defined criteria, and delivers instant notifications via Telegram.

---

## Features

- **Multi-source scraping** — monitoring multiple sites
- **Flexible filtering** — filter by various filter
- **Change detection** — tracks previously seen listings to notify only on new ones
- **Telegram notifications** — instant alerts sent to your Telegram bot
- **Scheduled execution** — configurable polling interval via Spring `@Scheduled`
- **Persistent storage** — PostgreSQL database for storing seen listings and user filters

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3 |
| Scraping | Jsoup |
| Scheduling | Spring `@Scheduled` |
| Database | PostgreSQL |
| Notifications | Telegram Bot API |
| Build | Maven |

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      Scheduler                          │
│              (runs every N minutes)                     │
└───────────────────────┬─────────────────────────────────┘
                        │
            ┌───────────▼───────────┐
            │    Scraper Service    │
            └───────────┬───────────┘
                        │
            ┌───────────▼───────────┐
            │   Filter Engine       │
            └───────────┬───────────┘
                        │
            ┌───────────▼───────────┐
            │  Change Detector      │
            └─────┬─────────┬───────┘
                  │         │
         new    found    already seen
                  │
      ┌───────────▼───────────┐
      │   Notification        │
      │   Service             │
      │   (Telegram Bot)      │
      └───────────────────────┘
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL
- Telegram Bot token (via [@BotFather](https://t.me/BotFather))

### Configuration

Create `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/condomonitor
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Telegram
telegram.bot.token=YOUR_BOT_TOKEN
telegram.chat.id=YOUR_CHAT_ID

# Scraping interval (milliseconds)
scraper.interval=300000
```

### Run

```bash
mvn clean install
mvn spring-boot:run
```

---

## Filter Configuration

Filters are defined in the database and can be updated at runtime. Example filter:

```json
{
  "minPrice": 50000,
  "maxPrice": 120000,
  "minArea": 40,
  "maxArea": 80,
  "location": "Novi Beograd",
  "type": "APARTMENT"
}
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/condomonitor/
│   │   ├── scraper/          # Site-specific scrapers (Jsoup)
│   │   ├── filter/           # Filter engine
│   │   ├── detector/         # Change detection logic
│   │   ├── notification/     # Telegram bot integration
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Spring Data repositories
│   │   └── scheduler/        # @Scheduled jobs
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/condomonitor/
```

---

## Telegram Bot Setup

1. Open Telegram and message [@BotFather](https://t.me/BotFather)
2. Run `/newbot` and follow the steps
3. Copy the token into `application.properties`
4. Start a conversation with your bot, then get your `chat_id`:
   ```
   https://api.telegram.org/bot<TOKEN>/getUpdates
   ```

---

## Bot Commands

| Command | Description |
|---|---|
| `/setfilter` | Set filter criteria (price, area, location, type) |
| `/showfilter` | Display current active filters |
| `/clearfilter` | Remove all filters |
| `/status` | Check if scraper is active |
| `/pause` | Pause monitoring |
| `/resume` | Resume monitoring |
| `/latest` | Show last N listings that matched your filter |
| `/help` | List all available commands |

---

