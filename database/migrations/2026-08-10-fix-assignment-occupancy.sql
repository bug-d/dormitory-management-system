-- 修复旧版本在申请阶段提前占床、审核阶段重复计数产生的历史数据。
-- 本脚本可重复执行；执行前请先备份数据库。

USE dormitory_management;

SET @active_bed_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'dorm_assignments'
      AND index_name = 'idx_active_bed'
);
SET @active_bed_index_sql = IF(
    @active_bed_index_exists = 0,
    'ALTER TABLE dorm_assignments ADD INDEX idx_active_bed (dorm_id, bed_no, status)',
    'SELECT 1'
);
PREPARE active_bed_index_stmt FROM @active_bed_index_sql;
EXECUTE active_bed_index_stmt;
DEALLOCATE PREPARE active_bed_index_stmt;

UPDATE dormitories d
LEFT JOIN (
    SELECT dorm_id, COUNT(*) AS active_count
    FROM dorm_assignments
    WHERE status = 'active'
    GROUP BY dorm_id
) active_assignments ON active_assignments.dorm_id = d.id
SET d.occupied = COALESCE(active_assignments.active_count, 0),
    d.status = CASE
        WHEN d.status IN ('maintenance', 'closed') THEN d.status
        WHEN COALESCE(active_assignments.active_count, 0) >= d.capacity THEN 'full'
        ELSE 'available'
    END,
    d.version = d.version + 1
WHERE d.occupied <> COALESCE(active_assignments.active_count, 0)
   OR (
       d.status NOT IN ('maintenance', 'closed')
       AND d.status <> CASE
           WHEN COALESCE(active_assignments.active_count, 0) >= d.capacity THEN 'full'
           ELSE 'available'
       END
   );
