-- =====================================================
-- ProcureGov - Tender Management System
-- Database Schema for Ministry of Public Works
-- Kingdom of Lesotho
-- =====================================================

-- Drop database if exists (for clean setup)
DROP DATABASE IF EXISTS procuregov_2333585;

-- Create database
CREATE DATABASE procuregov_2333585;
USE procuregov_2333585;

-- =====================================================
-- 1. USERS TABLE (Stores all system users)
-- =====================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    registration_number VARCHAR(50) UNIQUE,
    contact_number VARCHAR(20),
    physical_address TEXT,
    role ENUM('SUPPLIER', 'PROCUREMENT_OFFICER', 'EVALUATION_COMMITTEE') NOT NULL,
    account_locked BOOLEAN DEFAULT FALSE,
    failed_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_registration_number (registration_number)
);

-- =====================================================
-- 2. TENDERS TABLE (Main tender information)
-- =====================================================
CREATE TABLE tenders (
    tender_id INT PRIMARY KEY AUTO_INCREMENT,
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    category ENUM('Construction', 'Roads', 'Electrical', 'Plumbing', 'General Services') NOT NULL,
    description TEXT NOT NULL,
    estimated_value DECIMAL(15,2) NOT NULL,
    closing_datetime DATETIME NOT NULL,
    status ENUM('DRAFT', 'OPEN', 'CLOSED', 'UNDER_EVALUATION', 'EVALUATED', 'AWARDED') DEFAULT 'DRAFT',
    notice_pdf_path VARCHAR(500) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_reference (reference_number),
    INDEX idx_closing_datetime (closing_datetime),
    INDEX idx_category (category)
);

-- =====================================================
-- 3. BIDS TABLE (Supplier bid submissions)
-- =====================================================
CREATE TABLE bids (
    bid_id INT PRIMARY KEY AUTO_INCREMENT,
    tender_id INT NOT NULL,
    supplier_id INT NOT NULL,
    bid_amount DECIMAL(15,2) NOT NULL,
    technical_compliance TEXT NOT NULL,
    delivery_timeline INT NOT NULL COMMENT 'Number of days proposed',
    supporting_doc_path VARCHAR(500) NOT NULL,
    submission_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_winning_bid BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (tender_id) REFERENCES tenders(tender_id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_bid_per_tender (tender_id, supplier_id),
    INDEX idx_tender (tender_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_submission (submission_datetime)
);

-- =====================================================
-- 4. EVALUATIONS TABLE (Individual evaluator scores)
-- =====================================================
CREATE TABLE evaluations (
    evaluation_id INT PRIMARY KEY AUTO_INCREMENT,
    bid_id INT NOT NULL,
    evaluator_id INT NOT NULL,
    price_score DECIMAL(10,2) NOT NULL COMMENT 'Calculated automatically',
    technical_score DECIMAL(10,2) NOT NULL COMMENT 'Entered by evaluator (0-100)',
    delivery_score DECIMAL(10,2) NOT NULL COMMENT 'Calculated automatically',
    weighted_total DECIMAL(10,2) NOT NULL COMMENT 'Calculated automatically',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bid_id) REFERENCES bids(bid_id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_evaluator_bid (bid_id, evaluator_id),
    INDEX idx_bid (bid_id),
    INDEX idx_evaluator (evaluator_id)
);

-- =====================================================
-- 5. AWARDS TABLE (Contract awards)
-- =====================================================
CREATE TABLE awards (
    award_id INT PRIMARY KEY AUTO_INCREMENT,
    tender_id INT NOT NULL,
    winning_bid_id INT NOT NULL,
    awarded_value DECIMAL(15,2) NOT NULL,
    justification TEXT NOT NULL,
    award_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    awarded_by INT NOT NULL,
    FOREIGN KEY (tender_id) REFERENCES tenders(tender_id) ON DELETE CASCADE,
    FOREIGN KEY (winning_bid_id) REFERENCES bids(bid_id) ON DELETE CASCADE,
    FOREIGN KEY (awarded_by) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_tender_award (tender_id),
    INDEX idx_tender (tender_id),
    INDEX_idx_award_date (award_date)
);

-- =====================================================
-- 6. TENDER_STATUS_HISTORY TABLE (Audit trail)
-- =====================================================
CREATE TABLE tender_status_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    tender_id INT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by INT NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (tender_id) REFERENCES tenders(tender_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_tender_history (tender_id),
    INDEX_idx_changed_at (changed_at)
);

-- =====================================================
-- =====================================================
-- SEED DATA (For testing and demonstration)
-- =====================================================
-- =====================================================

-- Password for all seed accounts is: Password123
-- SHA-256 hash: 5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5

-- Procurement Officers (2 accounts)
INSERT INTO users (username, email, password_hash, full_name, registration_number, contact_number, physical_address, role, account_locked) VALUES
('officer.thabo', 'thabo.molapo@publicworks.gov.ls', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'Thabo Molapo', NULL, '+266 5888 1234', 'Ministry of Public Works, Maseru', 'PROCUREMENT_OFFICER', FALSE),
('officer.mamello', 'mamello.khitsane@publicworks.gov.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Mamello Khitsane', NULL, '+266 5888 5678', 'Ministry of Public Works, Maseru', 'PROCUREMENT_OFFICER', FALSE);

-- Evaluation Committee Members (2 accounts)
INSERT INTO users (username, email, password_hash, full_name, registration_number, contact_number, physical_address, role, account_locked) VALUES
('evaluator.lineo', 'lineo.makotoko@publicworks.gov.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Lineo Makotoko', NULL, '+266 5888 9012', 'Ministry of Public Works, Maseru', 'EVALUATION_COMMITTEE', FALSE),
('evaluator.mofihli', 'mofihli.sebotsa@publicworks.gov.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Mofihli Sebotsa', NULL, '+266 5888 3456', 'Ministry of Public Works, Maseru', 'EVALUATION_COMMITTEE', FALSE);

-- Supplier Accounts (3 accounts)
INSERT INTO users (username, email, password_hash, full_name, registration_number, contact_number, physical_address, role, account_locked) VALUES
('supplier.lesotho.construction', 'info@lesothoconstruction.co.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Lesotho Construction (Pty) Ltd', 'LC-2024-0001', '+266 5888 1111', 'Industrial Area, Maseru', 'SUPPLIER', FALSE),
('supplier.mountain.roads', 'tenders@mountainroads.co.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Mountain Roads Engineering', 'MRE-2024-0002', '+266 5888 2222', 'Mabote, Maseru', 'SUPPLIER', FALSE),
('supplier.electrical.solutions', 'bids@electricalsolutions.co.ls', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'Electrical Solutions Lesotho', 'ESL-2024-0003', '+266 5888 3333', 'Letsie Road, Maseru', 'SUPPLIER', FALSE);

-- Tenders (2 published tenders)
INSERT INTO tenders (reference_number, title, category, description, estimated_value, closing_datetime, status, notice_pdf_path, created_by) VALUES
('MPW-2024-0001', 'Rehabilitation of Maseru-Mafeteng Road', 'Roads', 'Rehabilitation of 45km stretch of road including drainage systems and road markings.', 45000000.00, DATE_ADD(NOW(), INTERVAL 30 DAY), 'OPEN', '/uploads/tenders/MPW-2024-0001_notice.pdf', 1),
('MPW-2024-0002', 'Construction of New Primary School - Thaba-Tseka', 'Construction', 'Construction of 6-classroom block with administration offices and sanitation facilities.', 12500000.00, DATE_ADD(NOW(), INTERVAL 20 DAY), 'OPEN', '/uploads/tenders/MPW-2024-0002_notice.pdf', 2);

-- Bids for Tender 1 (3 bids)
INSERT INTO bids (tender_id, supplier_id, bid_amount, technical_compliance, delivery_timeline, supporting_doc_path) VALUES
(1, 4, 42500000.00, 'Fully compliant with all technical specifications. Have experience with similar road projects in Lesotho.', 180, '/uploads/bids/bid_1_LC.pdf'),
(1, 5, 43000000.00, 'Complete compliance with tender requirements. Will use local subcontractors.', 200, '/uploads/bids/bid_2_MRE.pdf'),
(1, 6, 44500000.00, 'Technical proposal meets all requirements. Additional quality assurance measures proposed.', 210, '/uploads/bids/bid_3_ESL.pdf');

-- Bids for Tender 2 (2 bids)
INSERT INTO bids (tender_id, supplier_id, bid_amount, technical_compliance, delivery_timeline, supporting_doc_path) VALUES
(2, 4, 11800000.00, 'Full technical compliance. Experienced in school construction projects in Lesotho.', 240, '/uploads/bids/bid_4_LC.pdf'),
(2, 5, 12200000.00, 'Meets all specifications. Will employ local labor for construction.', 270, '/uploads/bids/bid_5_MRE.pdf');

-- Evaluations for Tender 1 (when tender becomes UNDER_EVALUATION)
-- Note: These will be added when the tender reaches evaluation stage

-- Display tables created confirmation
SELECT 'Database schema created successfully!' AS Status;
SELECT COUNT(*) AS TotalUsers FROM users;
SELECT COUNT(*) AS TotalTenders FROM tenders;
SELECT COUNT(*) AS TotalBids FROM bids;
