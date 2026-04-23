.PHONY: help doctor install build run clean test test-unit package verify

# -------- Config --------
MVN ?= mvn
JAVA_VERSION ?= 24

# On macOS, auto-pick a JDK (so Maven uses the same Java).
UNAME_S := $(shell uname -s 2>/dev/null)
ifeq ($(UNAME_S),Darwin)
  ifneq ("$(wildcard /usr/libexec/java_home)","")
    export JAVA_HOME ?= $(shell /usr/libexec/java_home -v $(JAVA_VERSION) 2>/dev/null)
    export PATH := $(JAVA_HOME)/bin:$(PATH)
  endif
endif

# -------- Targets --------
help: ## Show available commands
	@printf "\nLibConnect Make targets\n\n"
	@awk 'BEGIN {FS = ":.*##"; printf "Usage:\n  make <target>\n\nTargets:\n"} /^[a-zA-Z0-9_.-]+:.*##/ { printf "  %-14s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
	@printf "\n"

doctor: ## Print Java and Maven versions
	@java -version
	@$(MVN) -v

install: ## Download deps + compile (skips tests)
	@$(MVN) -DskipTests compile

build: ## Build jar (skips tests)
	@$(MVN) -DskipTests package

package: ## Alias for build
	@$(MVN) -DskipTests package

run: ## Run the JavaFX app
	@$(MVN) -DskipTests clean javafx:run

clean: ## Remove build outputs
	@$(MVN) clean

test: ## Run all tests
	@$(MVN) test

test-unit: ## Run tests (alias)
	@$(MVN) test

verify: ## Run full verification (tests + checks)
	@$(MVN) verify

