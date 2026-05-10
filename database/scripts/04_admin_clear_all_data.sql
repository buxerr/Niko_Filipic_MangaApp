-- Clears all application data and recreates the initial admin user.
-- Run manually when a database reset is needed.
-- This file is intentionally outside database/init so Docker does not execute it automatically.

CALL sp_clear_all_data();
