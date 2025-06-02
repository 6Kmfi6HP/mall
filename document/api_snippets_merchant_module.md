# Merchant Module API Snippets

This document provides snippets for API endpoints related to merchant management in the mall backend and portal.

## Merchant Onboarding APIs (mall-portal)

### 1. Merchant Registration Application

*   **Endpoint:** `POST /portal/merchants/register`
*   **Module:** `mall-portal`
*   **Description:** Allows a new potential merchant to submit their registration application, including company details and qualification documents.
*   **Request Body:**
    ```json
    {
      "name": "string (Merchant/Store Name, e.g., 'Awesome Gadgets Store')",
      "contact_name": "string (e.g., 'John Doe')",
      "contact_phone": "string (e.g., '+1234567890')",
      "contact_email": "string (email format, e.g., 'john.doe@example.com')",
      "address": "string (Full address, e.g., '123 Tech Lane, Silicon Valley, CA 94025')",
      "qualifications": {
        "business_license_no": "string (e.g., '12345ABCDE')",
        "business_license_image_url": "string (URL to uploaded business license image)",
        "legal_representative_id_front_url": "string (URL to ID front image)",
        "legal_representative_id_back_url": "string (URL to ID back image)"
      }
    }
    ```
*   **Success Response (201 Created):**
    ```json
    {
      "id": "integer (merchant_id)",
      "name": "string",
      "contact_name": "string",
      "contact_phone": "string",
      "contact_email": "string",
      "address": "string",
      "status": "integer (0 for Pending)",
      "create_time": "datetime",
      "message": "Merchant registration application submitted successfully. Your application is pending review."
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid input data (e.g., missing required fields, invalid email format).
        ```json
        {
          "error": "Validation Error",
          "details": {
            "name": ["Name is required."],
            "contact_email": ["Invalid email format."]
          }
        }
        ```
    *   `500 Internal Server Error`: Server-side processing error.
        ```json
        {
          "error": "Internal Server Error",
          "message": "An unexpected error occurred."
        }
        ```

### 2. Get Merchant Application Status

*   **Endpoint:** `GET /portal/merchants/applications/{applicationId}/status`
*   **Module:** `mall-portal`
*   **Description:** Allows an applicant to check the status of their submitted merchant registration application.
*   **Request Parameters:**
    *   `applicationId`: `integer` (Path Parameter, ID of the merchant application, which is `merchant_id`)
*   **Success Response (200 OK):**
    ```json
    {
      "application_id": "integer",
      "merchant_name": "string",
      "submission_date": "datetime",
      "status": "integer (0->Pending, 1->Approved, 2->Rejected, 3->Active, 4->Inactive)",
      "status_description": "string (e.g., 'Pending Review', 'Approved', 'Rejected')",
      "review_notes": "string (null if not applicable)"
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`: If the user is not authenticated to view this application.
    *   `404 Not Found`: Application with the given ID not found.
    *   `500 Internal Server Error`: Server-side processing error.

## Merchant Management APIs (mall-admin)

### 1. List Merchant Applications

*   **Endpoint:** `GET /admin/merchants/applications`
*   **Module:** `mall-admin`
*   **Description:** Retrieves a list of merchant applications, filterable by status.
*   **Request Parameters (Query):**
    *   `status`: `integer` (Optional, filter by status: 0->Pending, 1->Approved, 2->Rejected)
    *   `page`: `integer` (Optional, default: 1)
    *   `pageSize`: `integer` (Optional, default: 10)
*   **Success Response (200 OK):**
    ```json
    {
      "data": [
        {
          "id": "integer (merchant_id)",
          "name": "string",
          "contact_name": "string",
          "contact_email": "string",
          "submission_date": "datetime (from mer_merchant_qualification or mer_merchant create_time)",
          "status": "integer (0->Pending, 1->Approved, 2->Rejected)",
          "status_description": "string"
        }
      ],
      "total": "integer",
      "page": "integer",
      "pageSize": "integer"
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`: Admin not logged in.
    *   `403 Forbidden`: Admin does not have permission.
    *   `500 Internal Server Error`: Server-side processing error.

### 2. Review Merchant Application

*   **Endpoint:** `POST /admin/merchants/applications/{applicationId}/review`
*   **Module:** `mall-admin`
*   **Description:** Allows an admin to approve or reject a merchant application.
*   **Request Parameters (Path):**
    *   `applicationId`: `integer` (ID of the merchant application/merchant_id)
*   **Request Body:**
    ```json
    {
      "review_status": "integer (1 for Approved, 2 for Rejected)",
      "review_notes": "string (Required if rejected, optional if approved, e.g., 'Missing business license details.')",
      "level_id": "integer (Required if approved, FK to mer_merchant_level.id, e.g., 1 for '基础版')"
    }
    ```
*   **Success Response (200 OK):**
    ```json
    {
      "message": "Merchant application reviewed successfully. Status: {new_status}",
      "application_id": "integer",
      "new_status": "string (e.g., 'Approved', 'Rejected')"
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid input (e.g., missing `review_status`, or missing `level_id` if approved).
    *   `401 Unauthorized`: Admin not logged in.
    *   `403 Forbidden`: Admin does not have permission.
    *   `404 Not Found`: Application with the given ID not found.
    *   `409 Conflict`: Application already reviewed or in a state that cannot be reviewed.
    *   `500 Internal Server Error`: Server-side processing error.

### 3. Update Merchant Status

*   **Endpoint:** `PUT /admin/merchants/{merchantId}/status`
*   **Module:** `mall-admin`
*   **Description:** Allows an admin to activate or deactivate an approved merchant.
*   **Request Parameters (Path):**
    *   `merchantId`: `integer`
*   **Request Body:**
    ```json
    {
      "status": "integer (3 for Active, 4 for Inactive)"
    }
    ```
*   **Success Response (200 OK):**
    ```json
    {
      "message": "Merchant status updated successfully.",
      "merchant_id": "integer",
      "new_status": "integer (3 or 4)",
      "new_status_description": "string (e.g., 'Active', 'Inactive')"
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid status value or merchant not in a state to be activated/deactivated (e.g., still pending approval).
    *   `401 Unauthorized`: Admin not logged in.
    *   `403 Forbidden`: Admin does not have permission.
    *   `404 Not Found`: Merchant with the given ID not found.
    *   `500 Internal Server Error`.

### 4. Get Merchant Details

*   **Endpoint:** `GET /admin/merchants/{merchantId}`
*   **Module:** `mall-admin`
*   **Description:** Retrieves detailed information about a specific merchant, including their qualifications and current package.
*   **Request Parameters (Path):**
    *   `merchantId`: `integer`
*   **Success Response (200 OK):**
    ```json
    {
      "id": "integer",
      "name": "string",
      "contact_name": "string",
      "contact_phone": "string",
      "contact_email": "string",
      "address": "string",
      "status": "integer (0-4)",
      "status_description": "string",
      "create_time": "datetime",
      "update_time": "datetime",
      "qualifications": {
        "id": "integer",
        "business_license_no": "string",
        "business_license_image_url": "string",
        "legal_representative_id_front_url": "string",
        "legal_representative_id_back_url": "string",
        "submission_date": "datetime",
        "review_date": "datetime",
        "review_status": "integer (0-2)",
        "review_status_description": "string",
        "review_notes": "string"
      },
      "current_package": {
        "id": "integer",
        "level_name": "string (from mer_merchant_level)",
        "commission_rate": "decimal",
        "start_date": "datetime",
        "end_date": "datetime",
        "is_active": "boolean"
      }
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `404 Not Found`.
    *   `500 Internal Server Error`.

## Merchant Level & Package Management APIs (mall-admin)

### 1. Create Merchant Level

*   **Endpoint:** `POST /admin/merchant-levels`
*   **Module:** `mall-admin`
*   **Description:** Creates a new merchant level.
*   **Request Body:**
    ```json
    {
      "level_name": "string (e.g., 'Platinum Tier')",
      "description": "string (e.g., 'Exclusive benefits for top merchants')"
    }
    ```
*   **Success Response (201 Created):**
    ```json
    {
      "id": "integer",
      "level_name": "string",
      "description": "string",
      "create_time": "datetime",
      "update_time": "datetime"
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid input (e.g., `level_name` already exists).
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `500 Internal Server Error`.

### 2. List Merchant Levels

*   **Endpoint:** `GET /admin/merchant-levels`
*   **Module:** `mall-admin`
*   **Description:** Retrieves all available merchant levels.
*   **Success Response (200 OK):**
    ```json
    {
      "data": [
        {
          "id": "integer",
          "level_name": "string",
          "description": "string",
          "create_time": "datetime",
          "update_time": "datetime"
        }
      ]
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `500 Internal Server Error`.

### 3. Update Merchant Level

*   **Endpoint:** `PUT /admin/merchant-levels/{levelId}`
*   **Module:** `mall-admin`
*   **Description:** Updates an existing merchant level.
*   **Request Parameters (Path):**
    *   `levelId`: `integer`
*   **Request Body:**
    ```json
    {
      "level_name": "string (Optional)",
      "description": "string (Optional)"
    }
    ```
*   **Success Response (200 OK):**
    ```json
    {
      "id": "integer",
      "level_name": "string",
      "description": "string",
      "update_time": "datetime"
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid input.
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `404 Not Found`.
    *   `500 Internal Server Error`.

### 4. Assign/Update Merchant Package

*   **Endpoint:** `POST /admin/merchants/{merchantId}/package`
*   **Module:** `mall-admin`
*   **Description:** Assigns or updates a merchant's package (level and commission). This might create a new record in `mer_merchant_package` or update an existing one.
*   **Request Parameters (Path):**
    *   `merchantId`: `integer`
*   **Request Body:**
    ```json
    {
      "level_id": "integer (FK to mer_merchant_level.id)",
      "commission_rate": "decimal (e.g., 0.04 for 4%)",
      "start_date": "datetime (YYYY-MM-DDTHH:mm:ss)",
      "end_date": "datetime (YYYY-MM-DDTHH:mm:ss)",
      "is_active": "boolean (true/false)"
    }
    ```
*   **Success Response (200 OK or 201 Created):**
    ```json
    {
      "id": "integer (package_id)",
      "merchant_id": "integer",
      "level_id": "integer",
      "commission_rate": "decimal",
      "start_date": "datetime",
      "end_date": "datetime",
      "is_active": "boolean",
      "message": "Merchant package updated/assigned successfully."
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: Invalid input (e.g., `level_id` does not exist, invalid dates).
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `404 Not Found` (Merchant or Level not found).
    *   `500 Internal Server Error`.

### 5. Get Merchant Package History

*   **Endpoint:** `GET /admin/merchants/{merchantId}/packages`
*   **Module:** `mall-admin`
*   **Description:** Retrieves the history of packages for a specific merchant.
*   **Request Parameters (Path):**
    *   `merchantId`: `integer`
*   **Success Response (200 OK):**
    ```json
    {
      "data": [
        {
          "id": "integer (package_id)",
          "level_id": "integer",
          "level_name": "string",
          "commission_rate": "decimal",
          "start_date": "datetime",
          "end_date": "datetime",
          "is_active": "boolean",
          "create_time": "datetime"
        }
      ]
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`.
    *   `403 Forbidden`.
    *   `404 Not Found` (Merchant not found).
    *   `500 Internal Server Error`.
```
