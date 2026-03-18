#!/bin/bash

# Tutorly Testing Script
# This script runs all tests for the Tutorly platform

echo "🧪 Running Tutorly Test Suite"
echo "================================"

# Backend Tests
echo "📦 Running Backend Tests..."
cd tutorly

# Unit Tests
echo "  • Running Unit Tests..."
./mvnw.cmd test

# Integration Tests
echo "  • Running Integration Tests..."
./mvnw.cmd test -Dtest=**/integration/**Test

# Test Coverage Report
echo "  • Generating Test Coverage..."
./mvnw.cmd jacoco:report

cd ..

# Frontend Tests
echo "🎨 Running Frontend Tests..."
cd frontend

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "  • Installing dependencies..."
    npm install
fi

# Unit Tests
echo "  • Running Unit Tests..."
npm run test

# Test Coverage
echo "  • Generating Test Coverage..."
npm run test:coverage

# E2E Tests (if backend is running)
echo "  • Running E2E Tests..."
npm run test:e2e

cd ..

echo "✅ All tests completed!"
echo "📊 Test Reports:"
echo "  • Backend: tutorly/target/site/jacoco/index.html"
echo "  • Frontend: frontend/coverage/index.html"
echo "  • E2E: frontend/playwright-report/index.html"
