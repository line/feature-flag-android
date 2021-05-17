github.dismiss_out_of_range_messages({
  error: false,
  warning: true,
  message: true,
  markdown: true
})

Dir.glob("feature-flag/build/reports/ktlint/*/ktlint*SourceSetCheck.xml").each { |file|
  checkstyle_format.base_path = Dir.pwd
  checkstyle_format.report file.to_s
}

Dir.glob("feature-flag/build/test-results/test/*.xml").each { |file|
  junit.parse file
  junit.report
}

return unless status_report[:errors].empty?

return markdown "Build Failed" if ENV["JOB_STATUS"] != "success"
